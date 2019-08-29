package io.rsbox.net.impl.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec

/**
 * @author Kyle Escobar
 */

class HandshakeSeedCodec : Codec<HandshakeSeedResponse, HandshakeSeedResponse> {

    override fun encode(buf: ByteBuf, message: HandshakeSeedResponse): ByteBuf {
        buf.writeLong(message.seed)
        return buf
    }

    override fun decode(buf: ByteBuf): HandshakeSeedResponse {
        return HandshakeSeedResponse(-1L)
    }

}