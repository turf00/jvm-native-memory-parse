package com.bvb.jvm

import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AppNativeMemParseTest
{
    @Rule
    @JvmField
    var tmpFolder = TemporaryFolder()

    @Test
    fun shouldGenerateCsvOutputAsExpected()
    {
        val expected = classpathFile("/expected.txt")
        val srcFile = classpathFile("/example.txt")
        val destFile = tmpFolder.newFile()!!

        execute(srcFile.absolutePath!!, destFile.absolutePath!!)

        assertThat(expected).hasSameContentAs(destFile)
    }

    private fun classpathFile(fileName: String) = File(this.javaClass.getResource(fileName).file)

}