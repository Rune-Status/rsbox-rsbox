package io.rsbox.server.net.codec.login

import io.netty.buffer.ByteBuf
import io.rsbox.server.net.Codec
import io.rsbox.server.net.message.login.LoginRequest

/**
 * @author Kyle Escobar
 */

class LoginInboundCodec : Codec<LoginRequest> {

    override fun decode(buf: ByteBuf): LoginRequest {
        return LoginRequest()
    }

}