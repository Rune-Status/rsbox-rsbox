package io.rsbox.engine.service.login

import io.netty.channel.ChannelFutureListener
import io.rsbox.api.LoginStateResponse
import io.rsbox.api.event.EventManager
import io.rsbox.api.event.impl.PlayerAuthEvent
import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.LoginState
import io.rsbox.engine.net.game.pipeline.GamePacketDecoder
import io.rsbox.engine.net.game.pipeline.GamePacketEncoder
import io.rsbox.engine.net.pregame.login.LoginRequest
import io.rsbox.engine.net.pregame.login.LoginResponse
import io.rsbox.engine.service.Service
import io.rsbox.engine.system.login.LoginQueue
import mu.KLogging
import java.lang.IllegalArgumentException

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
        /**
         * Trigger Player Auth Event
         */
        EventManager.trigger(
            event = PlayerAuthEvent(player),
            logic = { loginContinue(player) },
            cancelLogic = { loginCancel(player, it) }
        )
    }

    private fun loginContinue(player: Player) {
        player.register()

        val response = LoginResponse(index = player.index, privilege = player.privilege)
        player.session.ctx.writeAndFlush(response)

        val p = player.session.ctx.pipeline()
        p.remove("login_codec")
        p.addBefore("handler", "packet_encoder", GamePacketEncoder(player.session.encodeRandom))
        p.addAfter("packet_encoder", "packet_decoder", GamePacketDecoder(player.session.decodeRandom))

        player.login()
    }

    private fun loginCancel(player: Player, event: PlayerAuthEvent) {
        val responses = LoginState.values().associate { it.id to it }
        val response = responses[event.loginStateResponse.id] ?: throw IllegalArgumentException("Login state id ${event.loginStateResponse.id} is invalid.")
        player.session.ctx.writeAndFlush(player.session.ctx.alloc().buffer(1).writeByte(response.id))
            .addListener(ChannelFutureListener.CLOSE)
    }

    companion object : KLogging()
}