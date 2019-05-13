@file:Suppress("NOTHING_TO_INLINE")

package kotlinx.io.buffer

import kotlinx.io.internal.*

/**
 * Represents a linear range of bytes.
 * All operations are guarded by range-checks by default however at some platforms they could be disabled
 * in release builds.
 *
 * Instance of this class has no additional state except the bytes themselves.
 */
expect class Buffer {
    /**
     * Size of buffer range in bytes.
     */
    val size: Int

    /**
     * Returns byte at [index] position.
     */
    inline fun loadByteAt(index: Int): Byte

    /**
     * Write [value] at the specified [index]
     */
    inline fun storeByteAt(index: Int, value: Byte)

    /**
     * Copies bytes from this buffer range from the specified [offset] and [length]
     * to the [destination] at [destinationOffset].
     * Copying bytes from a buffer to itself is allowed.
     */
    fun copyTo(destination: Buffer, offset: Int, length: Int, destinationOffset: Int)

    companion object {
        /**
         * Represents an empty buffer region
         */
        val Empty: Buffer
    }
}

/**
 * Read byte at the specified [index].
 */
inline operator fun Buffer.get(index: Int): Byte = loadByteAt(index)

/**
 * Read byte at the specified [index].
 */
inline operator fun Buffer.get(index: Long): Byte = loadByteAt(index.toIntOrFail { "index" })

/**
 * Index write operator to write [value] at the specified [index]
 */
inline operator fun Buffer.set(index: Long, value: Byte) = storeByteAt(index.toIntOrFail { "index" }, value)

/**
 * Index write operator to write [value] at the specified [index]
 */
inline operator fun Buffer.set(index: Int, value: Byte) = storeByteAt(index, value)

/**
 * Index write operator to write [value] at the specified [index]
 */
inline fun Buffer.storeByteAt(index: Long, value: UByte) = storeByteAt(index.toIntOrFail { "index" }, value.toByte())

/**
 * Index write operator to write [value] at the specified [index]
 */
inline fun Buffer.storeByteAt(index: Int, value: UByte) = storeByteAt(index, value.toByte())

/**
 * Fill buffer range starting at the specified [offset] with [value] repeated [count] times.
 */
expect fun Buffer.fill(offset: Long, count: Long, value: Byte)

/**
 * Fill buffer range starting at the specified [offset] with [value] repeated [count] times.
 */
expect fun Buffer.fill(offset: Int, count: Int, value: Byte)

/**
 * Copies bytes from this buffer range from the specified [offset] and [length]
 * to the [destination] at [destinationOffset].
 */
expect fun Buffer.copyTo(destination: ByteArray, offset: Int, length: Int, destinationOffset: Int = 0)

/**
 * Copies bytes from this buffer range from the specified [offset] and [length]
 * to the [destination] at [destinationOffset].
 */
expect fun Buffer.copyTo(destination: ByteArray, offset: Long, length: Int, destinationOffset: Int = 0)

/**
 * Returns byte at [index] position.
 */
inline fun Buffer.loadByteAt(index: Long): Byte = loadByteAt(index.toIntOrFail { "index" })

/**
 * Write [value] at the specified [index].
 */
inline fun Buffer.storeByteAt(index: Long, value: Byte) = storeByteAt(index.toIntOrFail { "index" }, value)

/**
 * Copies bytes from this buffer range from the specified [offset] and [length]
 * to the [destination] at [destinationOffset].
 * Copying bytes from a buffer to itself is allowed.
 */
fun Buffer.copyTo(destination: Buffer, offset: Long, length: Long, destinationOffset: Long) =
    copyTo(destination, offset.toIntOrFail { "offset" }, length.toIntOrFail { "length" }, destinationOffset.toIntOrFail { "destinationOffset" })

/**
 * Execute [block] of code providing a temporary instance of [Buffer] view of this byte array range
 * starting at the specified [offset] and having the specified bytes [length].
 * By default, if neither [offset] nor [length] specified, the whole array is used.
 * An instance of [Buffer] provided into the [block] should be never captured and used outside of lambda.
 */
expect fun <R> ByteArray.useBuffer(offset: Int = 0, length: Int = size - offset, block: (Buffer) -> R): R

