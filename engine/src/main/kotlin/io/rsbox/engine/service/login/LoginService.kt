package io.rsbox.engine.service.login

import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.pregame.login.LoginRequest
import io.rsbox.engine.net.pregame.login.LoginResponse
import io.rsbox.engine.service.Service
import io.rsbox.engine.system.login.LoginQueue
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class LoginService : Service {

    private val loginQueue = LoginQueue(this)

    override fun start() {
        loginQueue.startProcessor()
    }

    override fun stop() {
        loginQueue.stopProcessor()
    }

    fun queueLoginRequest(request: LoginRequest) {
        loginQueue.queue.offer(request)
        logger.info("Login request for username {} has been queued.", request.username)
    }

    fun handleLoginSuccess(player: Player) {
        player.register()

        val response = LoginResponse(index = player.index, privilege = player.privilege)
        player.session.ctx.writeAndFlush(response)

        val p = player.session.ctx.pipeline()
        p.remove("login_codec")

        player.login()
    }

    companion object : KLogging()
}