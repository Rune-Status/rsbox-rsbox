package io.rsbox.server.net.codec.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.server.net.message.handshake.HandshakeRequest

/**
 * @author Kyle Escobar
 */

/**
 * The decode logic for inbound handshake requests.
 */
class HandshakeInboundCodec : Codec<HandshakeRequest> {

    /**
     * This is the logic that gets executed to decode a buffer into
     * a handshake request.
     * When the handshake request is JS5, an additional revision step is required.
     *
     * @param buf The inbound buffer.
     * @return The decoded handshake request.
     */
    override fun decode(buf: ByteBuf): HandshakeRequest {
        buf.resetReaderIndex()

        val opcode = buf.readUnsignedByte().toInt()
        return when(opcode) {
            15 -> decodeJS5Handshake(buf)
            else -> HandshakeRequest(-1)
        }
    }

    private fun decodeJS5Handshake(buf: ByteBuf): HandshakeRequest {
        val revision = buf.readInt()
        return HandshakeRequest(15, revision)
    }

}