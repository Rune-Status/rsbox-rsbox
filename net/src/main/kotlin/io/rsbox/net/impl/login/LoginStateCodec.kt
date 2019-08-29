package io.rsbox.net.impl.login

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.net.LoginState

/**
 * @author Kyle Escobar
 */

class LoginStateCodec : Codec<LoginStateResponse, LoginStateResponse> {

    override fun encode(buf: ByteBuf, message: LoginStateResponse): ByteBuf {
        buf.writeByte(message.state.id)
        return buf
    }

    override fun decode(buf: ByteBuf): LoginStateResponse {
        return LoginStateResponse(LoginState.COULD_NOT_COMPLETE_LOGIN)
    }

}