package io.rsbox.engine.service.impl.login

import io.rsbox.engine.Engine
import io.rsbox.engine.login.LoginQueue
import io.rsbox.net.LoginState
import io.rsbox.net.impl.login.LoginStateResponse
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class LoginProcessor : Runnable {

    override fun run() {
        while(true) {
            val request = LoginQueue.queue.take()
            try {
                val session = Engine.networkServer.getLoginRequestSession(request) ?: throw Exception("Unable to identify login request session.")
                session.writeMessage(LoginStateResponse(LoginState.SERVER_UPDATE))
            } catch(e : Exception) {
                logger.error("Error processing login request from queue.", e)
            }
        }
    }

    companion object : KLogging()
}