package io.rsbox.server.net.codec.js5

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.server.net.message.js5.JS5Request

/**
 * @author Kyle Escobar
 */

/**
 * The logic for decoding the inbound buffer into a JS5 request.
 * NOTE. In order to check the opcode, This class, resets the buffer's reader index,
 * Re-reads the opcode and marks the reader index again.
 *
 * Opcodes 2, 3, and 6 are ignored. They are 3 byte client statuses which tell the server
 * the current state of the client. They are not needed and the buffer skips those bytes.
 *
 * Opcode 0 = PRIORITY REQUEST
 * Opcode 1 = NORMAL REQUEST
 */
class JS5InboundCodec : Codec<JS5Request> {

    override fun decode(buf: ByteBuf): JS5Request {
        buf.resetReaderIndex()
        val opcode = buf.readByte().toInt()
        buf.markReaderIndex()

        return if(opcode == 2 || opcode == 3 || opcode == 6) {
            buf.skipBytes(3)

            // Used to signal to the handler to not process this request.
            JS5Request(ignore = true)
        } else {
            val index = buf.readUnsignedByte().toInt()
            val archive = buf.readUnsignedShort()

            JS5Request(index, archive, opcode == 0)
        }
    }

}