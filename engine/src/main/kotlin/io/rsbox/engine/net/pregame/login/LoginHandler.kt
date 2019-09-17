package io.rsbox.engine.net.pregame.login

import io.rsbox.engine.net.Session
import io.rsbox.engine.service.ServiceManager
import io.rsbox.engine.service.login.LoginService

/**
 * @author Kyle Escobar
 */

class LoginHandler {

    @Suppress("UNUSED_PARAMETER")
    fun handle(session: Session, message: LoginRequest) {
        ServiceManager[LoginService::class.java]!!.queueLoginRequest(message)
    }

}