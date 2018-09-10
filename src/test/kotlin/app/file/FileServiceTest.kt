package app.file

import app.generator.generateData
import app.sha1sum
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FileServiceTest {
    private val service = FileService(
        Jimfs.newFileSystem(Configuration.unix()).getPath("/")
    )

    @Test
    fun load() {
        val data = generateData()
        val name = "foo.dat"
        service.save(name, data)
        val savedData = service.load(name).block()!!.readAllBytes()
        assertArrayEquals(data, savedData)
    }

    @Test
    fun save() {
        val data = generateData()
        val name = "foo.dat"
        service.save(name, data)
        val savedData = service.load(name).block()!!.readAllBytes()
        assertArrayEquals(data, savedData)
    }

    @Test
    fun check() {
        val data = generateData()
        val name = "foo.dat"
        service.save(name, data)
        val hash = sha1sum(data)
        assertTrue(service.check(name, hash))
    }

}
