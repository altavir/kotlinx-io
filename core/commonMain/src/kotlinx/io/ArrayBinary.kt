package kotlinx.io

import kotlinx.io.buffer.DEFAULT_BUFFER_SIZE
import kotlin.math.min

/**
 * A binary wrapping existing array
 */
@ExperimentalIoApi
inline class ArrayBinary(val array: ByteArray) : RandomAccessBinary {
    override val size: Int get() = array.size

    override fun <R> read(from: Int, atMost: Int, block: Input.() -> R): R {
        return ByteArrayInput(
            array,
            from,
            min(from + atMost, array.size)
        ).use(block)
    }

    companion object {
        fun write(bufferSize: Int = DEFAULT_BUFFER_SIZE, builder: Output.() -> Unit): ArrayBinary {
            val bytes = buildBytes(bufferSize, builder)
            val array = bytes.toByteArray()
            bytes.close()
            return array.asBinary()
        }
    }
}

@ExperimentalIoApi
public fun ByteArray.asBinary(): ArrayBinary = ArrayBinary(this)

@ExperimentalIoApi
public fun <R> ByteArray.read(block: Input.() -> R): R = asBinary().read(block)