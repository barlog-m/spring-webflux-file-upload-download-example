package app.file

import app.sha1sum
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

@Service
open class FileService(
    private val base: Path
) {
    open fun load(name: String): Mono<InputStream> {
        return Mono.defer { Mono.just(Files.newInputStream(path(name))) }
    }

    open fun save(file: Mono<FilePart>): Mono<Void> =
        file.map { FilePartDecorator(it) }
            .flatMap {
               it.save(path(it.filename()))
            }

    open fun save(name: String, data: ByteArray) {
        Files.newOutputStream(path(name)).use {
            data.inputStream().copyTo(it)
        }
    }

    open fun check(name: String, hash: String): Boolean {
        return hash == sha1sum(path(name))
    }

    private fun path(name: String): Path = base.resolve(name)
}
