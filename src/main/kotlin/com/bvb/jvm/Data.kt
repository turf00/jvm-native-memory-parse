package com.bvb.jvm

data class Memory(val name: String,
                  val reserved: Long,
                  val committed: Long,
                  val notes: String)
{
    fun toString(delimiter: Char): String
    {
        val mbReserved = reserved / 1024L
        val mbCommitted = committed / 1024L
        return "$name$delimiter$reserved$delimiter$mbReserved$delimiter$committed$delimiter$mbCommitted$delimiter$notes"
    }
}

