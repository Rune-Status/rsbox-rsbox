package io.rsbox.server.net.codec.js5

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.server.net.message.js5.JS5Response

/**
 * @author Kyle Escobar
 */

class JS5OutboundCodec : Codec<JS5Response> {

    override fun encode(buf: ByteBuf, message: JS5Response): ByteBuf {
        buf.writeByte(message.index)
        buf.writeShort(message.archive)

        message.data.forEach { data ->
            if(buf.writerIndex() % 512 == 0) {
                buf.writeByte(-1)
            }
            buf.writeByte(data.toInt())
        }

        return buf
    }

}