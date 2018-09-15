package com.bvb.jvm

import java.io.BufferedOutputStream
import java.io.Closeable
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

private val PATTERN_INDIVIDUAL_TYPES = Pattern.compile("""^-\s+(.+)\s\(reserved=(\d+)KB, committed=(\d+)KB.+""")
private val PATTERN_TOTAL = Pattern.compile("""^Total: reserved=(\d+)KB, committed=(\d+)KB""")

fun main(args: Array<String>)
{
    if (args.size != 2)
    {
        println("Expected 2 arguments: <input file> <output file>")
    }
    else
    {
        execute(args[0], args[1])
    }
}

internal fun execute(fileIn: String, fileOut: String)
{
    val mem = parseFile(fileIn)

    BufferedOutputStream(FileOutputStream(fileOut)).use {
        val writer = it.writer(Charset.defaultCharset())
        writer.write("Type,Reserved (KB),Reserved (MB),Committed (KB), Committed (MB),Notes\n")
        mem.forEach { m -> writer.write(m.toString(',') + "\n") }
        writer.flush()
        closeIgnoreEx(writer)
    }
}

private fun parseFile(file: String): List<Memory>
{
    val lineMatcher = LineMatcher()

    return Files.readAllLines(Paths.get(file)).mapNotNull {
        lineMatcher.parseLine(it)
    }
}

private enum class Position
{
    BEFORE_TOTAL,
    AFTER_TOTAL,
    LINE_AFTER_TYPE_MATCH
}

private class LineMatcher
{
    private var position = Position.BEFORE_TOTAL

    fun parseLine(l: String): Memory?
    {
        when (position)
        {
            Position.AFTER_TOTAL -> {
                val matcher = PATTERN_INDIVIDUAL_TYPES.matcher(l)
                if (matcher.matches())
                {
                    return Memory(matcher.group(1), matcher.group(2).toLong(), matcher.group(3).toLong(),"")
                }
            }
            Position.BEFORE_TOTAL -> {
                position = Position.AFTER_TOTAL
                val matcher = PATTERN_TOTAL.matcher(l)
                if (matcher.matches())
                {
                    position = Position.LINE_AFTER_TYPE_MATCH
                    return Memory("Total", matcher.group(1).toLong(), matcher.group(2).toLong(), "")
                }
            }
            Position.LINE_AFTER_TYPE_MATCH -> {
                position = Position.AFTER_TOTAL
            }
        }
        return null
    }
}

private fun closeIgnoreEx(closeable: Closeable) {
    try
    {
        closeable.close()
    }
    catch (ex: IOException)
    {
        // ignore
    }
}
