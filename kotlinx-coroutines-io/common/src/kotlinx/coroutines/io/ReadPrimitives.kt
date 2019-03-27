@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package kotlinx.coroutines.io

import kotlinx.io.bits.*
import kotlinx.io.core.*

suspend fun ByteReadChannel.readShort(): Short {
    return readPrimitiveTemplate(2, 0, { loadShortAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readUShort(): UShort {
    return readPrimitiveTemplate(2, 0u, { loadUShortAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readInt(): Int {
    return readPrimitiveTemplate(4, 0, { loadIntAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readUInt(): UInt {
    return readPrimitiveTemplate(4, 0u, { loadUIntAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readLong(): Long {
    return readPrimitiveTemplate(8, 0, { loadLongAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readULong(): ULong {
    return readPrimitiveTemplate(8, 0uL, { loadULongAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readFloat(): Float {
    return readPrimitiveTemplate(4, Float.NaN, { loadFloatAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readDouble(): Double {
    return readPrimitiveTemplate(8, Double.NaN, { loadDoubleAt(it) }, { reverseByteOrder() })
}

suspend fun ByteReadChannel.readFully(dst: ByteArray, offset: Int = 0, length: Int = dst.size - offset) {
    var dstPosition = offset
    val dstEnd = dstPosition + length

    while (dstPosition < dstEnd) {
        read { source, start, endExclusive ->
            val partLength = minOf((dstEnd - dstPosition).toLong(), endExclusive - start).toInt()
            source.copyTo(dst, start, partLength, dstPosition)
            dstPosition += partLength
            partLength
        }
    }
}

private suspend inline fun <T> ByteReadChannel.readPrimitiveTemplate(
    sizeInBytes: Int,
    initial: T,
    read: Memory.(offset: Long) -> T,
    reverse: T.() -> T
): T {
    var value: T = initial

    read(sizeInBytes) { source, start, endExclusive ->
        if (endExclusive - start < sizeInBytes) {
            notEnoughBytes(sizeInBytes)
        }

        value = read(source, start)
        sizeInBytes
    }

    @Suppress("DEPRECATION_ERROR")
    return when (readByteOrder) {
        ByteOrder.BIG_ENDIAN -> value
        else -> value.reverse()
    }
}

private fun notEnoughBytes(sizeInBytes: Int): Nothing =
    throw EOFException("Not enough bytes to read $sizeInBytes primitive.")
