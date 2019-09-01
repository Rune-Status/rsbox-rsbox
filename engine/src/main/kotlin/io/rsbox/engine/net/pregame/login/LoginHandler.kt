package io.rsbox.engine.net.pregame.login

import io.rsbox.engine.net.Session

/**
 * @author Kyle Escobar
 */

class LoginHandler {

    fun handle(session: Session, message: LoginRequest) {
        println("BOOOOM username=${message.username}, password=${message.password}")
    }

}