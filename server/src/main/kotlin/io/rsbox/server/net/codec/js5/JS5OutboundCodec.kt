package io.rsbox.server.net.codec.js5

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.server.net.message.js5.JS5Response

/**
 * @author Kyle Escobar
 */

/**
 * Encodes the JS5 response into a byte buffer to be send over the network.
 * NOTE Every 512 bytes, the client requires a -1 byte value to be sent.
 * Executing % 512 is the modulus (remainder) of doing buf.writerIndex() / 512
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