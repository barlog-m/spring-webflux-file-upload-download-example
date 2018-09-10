package app.file

import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import java.io.IOException
import java.io.InputStream
import java.nio.channels.FileChannel
import java.nio.channels.Channels
import java.nio.file.StandardOpenOption
import java.nio.file.Path

class FilePartDecorator(
    private val part: FilePart
) {
    companion object {
        private val FILE_CHANNEL_OPTIONS = arrayOf(
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )
    }

    fun filename(): String = part.filename()

    fun save(path: Path): Mono<Void> =
        DataBufferUtils.join(part.content()).flatMap {
            transferTo(path, it.asInputStream())
        }

    private fun transferTo(path: Path, inputStream: InputStream): Mono<Void> {
        Channels.newChannel(inputStream).use { input ->
            FileChannel.open(path, *FILE_CHANNEL_OPTIONS).use { output ->
                try {
                    val size = (input as? FileChannel)?.size()
                        ?: java.lang.Long.MAX_VALUE
                    var totalWritten: Long = 0
                    while (totalWritten < size) {
                        val written = output!!.transferFrom(input, totalWritten, size - totalWritten)
                        if (written <= 0) {
                            break
                        }
                        totalWritten += written
                    }
                } catch (ex: IOException) {
                    return Mono.error(ex)
                }
            }
        }
        return Mono.empty()
    }
}
