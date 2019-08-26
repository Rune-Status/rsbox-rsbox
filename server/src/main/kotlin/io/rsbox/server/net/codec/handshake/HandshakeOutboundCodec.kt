package io.rsbox.server.net.codec.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.server.net.message.handshake.HandshakeResponse

/**
 * @author Kyle Escobar
 */

/**
 * Encodes the handshake response into a byte buffer to be
 * sent over the network.
 */
class HandshakeOutboundCodec : Codec<HandshakeResponse> {

    override fun encode(buf: ByteBuf, message: HandshakeResponse): ByteBuf {
        buf.writeByte(message.type.id)
        return buf
    }

}