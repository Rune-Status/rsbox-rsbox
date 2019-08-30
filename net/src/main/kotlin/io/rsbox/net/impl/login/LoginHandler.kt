package io.rsbox.net.impl.login

import io.rsbox.engine.login.GameLoginRequest
import io.rsbox.engine.login.LoginQueue
import io.rsbox.net.MessageHandler
import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

class LoginHandler : MessageHandler<Session, LoginRequest> {

    override fun handle(session: Session, message: LoginRequest) {
        when(message.error) {
            true -> handleLoginFailure(session, message)
            else -> handleLoginSuccess(session, message)
        }
    }

    private fun handleLoginSuccess(session: Session, message: LoginRequest) {
        val request = GameLoginRequest(
            username = message.username,
            password = message.password,
            revision = message.revision,
            xteaKeys = message.xteaKeys,
            reconnecting = message.reconnecting,
            uuid = message.uuid,
            authCode = message.authCode,
            viewportResizable = message.viewportResizable,
            viewportWidth = message.viewportWidth,
            viewportHeight = message.viewportHeight,
            seed = message.seed
        )

        LoginQueue.addLoginRequeust(request, session.ctx.channel().remoteAddress())

        session.lastLoginRequest = request
    }

    private fun handleLoginFailure(session: Session, message: LoginRequest) {
        session.writeMessage(LoginStateResponse(message.errorResponse!!))
    }
}