package app.file

import app.generator.generateData
import app.kExpectBody
import app.kIsEqualTo
import com.google.common.jimfs.Configuration as JimFsConfiguration
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.nio.file.Path
import org.springframework.http.client.MultipartBodyBuilder

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [FileControllerIntegrationTest.FileControllerIntegrationTest::class],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class FileControllerIntegrationTest {
    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = ["app"])
    open class FileControllerIntegrationTest {
        @Bean
        open fun path(): Path = Jimfs
            .newFileSystem(JimFsConfiguration.unix()).getPath("/")
    }

    @Autowired
    private lateinit var webClient: WebTestClient

    private fun upload(name: String, data: ByteArray): WebTestClient.ResponseSpec {
        val body = MultipartBodyBuilder().apply {
            part("file", object : ByteArrayResource(data) {
                override fun getFilename(): String {
                    return name
                }
            })
        }.build()

        return webClient
            .post()
            .uri("/upload")
            .body(BodyInserters.fromMultipartData(body))
            .exchange()
    }

    @Test
    fun upload() {
        val name = "bar.dat"
        val data = generateData()
        upload(name, data)
            .expectStatus().isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun download() {
        val name = "bar.dat"
        val data = generateData()

        upload(name, data)
            .expectStatus().isEqualTo(HttpStatus.CREATED)

        webClient
            .get()
            .uri("/download/$name")
            .exchange()
            .expectStatus().isOk
            .kExpectBody<ByteArray>()
            .kIsEqualTo(data)
    }
}
