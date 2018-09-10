package app

import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

private const val HASH = "SHA-1"

fun sha1sum(data: ByteArray): String {
    val md = MessageDigest.getInstance(HASH);
    md.update(data)
    return array2str(md.digest())
}

fun sha1sum(filePath: Path): String {
    val md = MessageDigest.getInstance(HASH);

    var buffer = ByteArray(1024 * 100)

    BufferedInputStream(Files.newInputStream(filePath)).use {
        var i = it.read(buffer)
        while (i != -1) {
            md.update(buffer, 0, i)
            i = it.read(buffer)
        }
    }
    return array2str(md.digest())
}

/**
Byte Array to Hex String converter
 */
fun array2str(data: ByteArray): String {
    val r = StringBuffer()
    for (i in data) {
        r.append(String.format("%02x", i))
    }
    return r.toString()
}
