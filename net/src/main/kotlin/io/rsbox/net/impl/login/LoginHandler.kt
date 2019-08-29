package io.rsbox.net.impl.login

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
        println(message.toString())
    }

    private fun handleLoginFailure(session: Session, message: LoginRequest) {
        session.writeMessage(LoginStateResponse(message.errorResponse!!))
    }
}