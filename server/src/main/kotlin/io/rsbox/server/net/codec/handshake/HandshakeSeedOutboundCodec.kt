package io.rsbox.server.net.codec.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.server.net.Codec
import io.rsbox.server.net.message.handshake.HandshakeSeedResponse

/**
 * @author Kyle Escobar
 */

/**
 * Encodes the seed to be sent to the client from the seed message.
 * The seed is used to generate a shared random value using the RSA.
 * This is then used to encrypt password and machine information during the login
 * protocol.
 */
class HandshakeSeedOutboundCodec : Codec<HandshakeSeedResponse> {

    override fun encode(buf: ByteBuf, message: HandshakeSeedResponse): ByteBuf {
        buf.writeLong(message.seed)
        return buf
    }

}