package io.rsbox.engine.system.login

import io.netty.channel.ChannelFutureListener
import io.rsbox.api.event.Event
import io.rsbox.api.event.impl.PlayerAuthEvent
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.net.LoginState
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.pregame.login.LoginRequest
import io.rsbox.engine.system.serializer.player.PlayerCreator
import io.rsbox.engine.system.serializer.player.PlayerLoader
import io.rsbox.util.IsaacRandom
import java.io.File

/**
 * @author Kyle Escobar
 */

class LoginWorker(private val loginQueue: LoginQueue) : Runnable {

    private val autoCreate = Conf.SERVER[ServerSpec.auto_create_accounts]

    override fun run() {
        while(true) {
            val request = loginQueue.queue.take()
            if(request != null) {
                try {
                    this.processRequest(request)
                } catch(e : Exception) {
                    e.printStackTrace()
                    sendLoginState(request.session, LoginState.NO_DATA_LOGINSERVER)
                }
            }
        }
    }

    private fun processRequest(request: LoginRequest) {
        // Strip the username
        val username = request.username.replace(Regex("[^a-zA-Z0-9.-]"), "_")

        // Check if username exists
        val file = File("${PathConstants.PLAYER_SAVES_PATH}$username.yml")
        if(!file.exists()) {
            if(autoCreate) {
                PlayerCreator.createPlayer(username, request.password)
            } else {
                LoginQueue.logger.info("Login request rejected for username {} due to INVALID_CREDENTIALS.", username)
                sendLoginState(request.session, LoginState.INVALID_CREDENTIALS)
                return
            }
        }

        // Check Credentials
        if(PlayerLoader.checkCredentials(username, request.password)) {

            val p = PlayerLoader.loadPlayer(username, request.session)!!
            if(p.banned) {
                LoginQueue.logger.info("Login request rejected for username {} due to ACCOUNT_BANNED.")
                sendLoginState(request.session, LoginState.ACCOUNT_BANNED)
                return
            }

            val decodeRandom = IsaacRandom(request.xteaKeys)
            val encodeRandom = IsaacRandom(IntArray(request.xteaKeys.size) { request.xteaKeys[it] + 50 } )

            request.session.encodeRandom = encodeRandom
            request.session.decodeRandom = decodeRandom

            request.session.player = p

            val event = Event.trigger(PlayerAuthEvent(p)) {
                loginQueue.service.handleLoginSuccess(p)
                LoginQueue.logger.info("Login request accepted for username {}. Handing off connection to game protocol.", username)
            }

            if(event.isCancelled()) {
                val state: LoginState
                val stateMap = LoginState.values().associate { it.id to it }
                state = if(!stateMap.containsKey(event.loginStateId)) {
                    LoginState.COULD_NOT_COMPLETE_LOGIN
                } else {
                    stateMap.getValue(event.loginStateId)
                }
                sendLoginState(request.session, state)
                LoginQueue.logger.info("Login request rejected for username {} due to {}.", username, state)
            }

            return
        }

        LoginQueue.logger.info("Login request rejected for username {} due to INVALID_CREDENTIALS.", username)
        sendLoginState(request.session, LoginState.INVALID_CREDENTIALS)
        return
    }

    private fun sendLoginState(session: Session, state: LoginState) {
        session.ctx.writeAndFlush(session.ctx.alloc().buffer(1).writeByte(state.id))
            .addListener(ChannelFutureListener.CLOSE)
    }
}