package app.file

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class FileController(
    private val service: FileService
) {
    @PostMapping("/upload",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(@RequestPart("file") file: Mono<FilePart>): Mono<Void> {
        return service.save(file)
    }

    @GetMapping("/download/{name}")
    fun download(@PathVariable name: String): Mono<ResponseEntity<InputStreamResource>> =
        service.load(name).map {
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$name\"")
                .body(InputStreamResource(it))
        }
}
