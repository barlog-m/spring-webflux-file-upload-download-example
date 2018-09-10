package app.file

import app.generator.generateData
import app.kExpectBody
import app.kIsEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import java.io.InputStream

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@WebFluxTest(FileController::class)
class FileControllerTest {
    @Autowired
    private lateinit var webClient: WebTestClient

    @MockBean
    private lateinit var service: FileService

    @Test
    fun upload() {
        val name = "bar.dat"
        val data = generateData()

        val body = MultipartBodyBuilder().apply {
            part("file", object : ByteArrayResource(data) {
                override fun getFilename(): String {
                    return name
                }
            })
        }.build()

        webClient
            .post()
            .uri("/upload")
            .body(BodyInserters.fromMultipartData(body))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun download() {
        val name = "bar.dat"
        val data = generateData()

        given(service.load(name))
            .willReturn(Mono.just<InputStream>(data.inputStream()))

        webClient
            .get()
            .uri("/download/$name")
            .exchange()
            .expectStatus().isOk
            .kExpectBody<ByteArray>()
            .kIsEqualTo(data)
    }
}
