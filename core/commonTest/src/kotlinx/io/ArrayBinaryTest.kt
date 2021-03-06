package kotlinx.io

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ArrayBinaryTest {

    val bytes = ByteArray(128) { it.toByte() }

    val binary = bytes.asBinary()

    @Test
    fun testFullFileRead() {
        binary.read {
            discard(3)
            assertReadByte(3)
            assertFails {
                discardExact(128)
            }
        }
    }

    @Test
    fun testStandAloneBinary() {
        val sub = binary.read {
            discard(10)
            readBinary(20)
        }

        assertEquals(20, sub.size)

        sub.read {
            readByteArray(20)
            assertFails {
                readByte()
            }
        }

        //check second read
        sub.read {
            val res = readByteArray(10)
            assertEquals(10,res.size)
            assertEquals(12, res[2])
            assertEquals(20,readByte())
        }
    }
}