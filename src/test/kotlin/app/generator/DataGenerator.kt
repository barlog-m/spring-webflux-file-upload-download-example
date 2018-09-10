package app.generator

import java.util.concurrent.ThreadLocalRandom

fun generateData(): ByteArray {
    val random = ThreadLocalRandom.current()
    val data = ByteArray(1024 * 984)
    random.nextBytes(data)
    return data
}
