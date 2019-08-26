package io.rsbox.server.net.codec.js5

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.server.net.message.js5.JS5Request

/**
 * @author Kyle Escobar
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