package io.rsbox.engine.service.impl.login

import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.Engine
import io.rsbox.engine.login.LoginQueue
import io.rsbox.engine.system.serializer.player.PlayerCreator
import io.rsbox.engine.system.serializer.player.PlayerLoader
import io.rsbox.net.LoginState
import io.rsbox.net.impl.login.LoginStateResponse
import io.rsbox.util.IsaacRandom
import mu.KLogging
import java.io.File

/**
 * @author Kyle Escobar
 */

class LoginProcessor : Runnable {

    override fun run() {
        while(true) {
            val request = LoginQueue.queue.take()
            try {
                val session = Engine.networkServer.getLoginRequestSession(request) ?: throw Exception("Unable to identify login request session.")

                /**
                 * Check if the player's username exists in the save folder, Also check if we
                 * need to auto create an account from the config.
                 */
                val autoCreate = Conf.SERVER[ServerSpec.auto_create_accounts]

                /**
                 * Remove all unsafe file name chars for security.
                 */
                val username = request.username.replace(Regex("[^a-zA-Z0-9.-]"), "_")
                if(!File("${PathConstants.PLAYER_SAVES_PATH}$username.yml").exists() && autoCreate) {
                    PlayerCreator.createPlayer(username, request.password)
                }

                /**
                 * Login the player.
                 */
                val decodeRandom = IsaacRandom(request.xteaKeys)
                val encodeRandom = IsaacRandom(IntArray(request.xteaKeys.size) { request.xteaKeys[it] + 50 } )

                if(PlayerLoader.checkCredentials(username, request.password)) {
                    PlayerLoader.loadPlayer(username, session)?.login(encodeRandom, decodeRandom)
                    logger.info("Login request accepted for username {}. Handing session off to game protocol.", username)
                } else {

                    val response = LoginStateResponse(LoginState.INVALID_CREDENTIALS)
                    session.writeMessage(response)

                    logger.info("Login request rejected for username {} with reason: {}.", username, LoginState.INVALID_CREDENTIALS)
                }
            } catch(e : Exception) {
                logger.error("Error processing login request from queue.", e)
            }
        }
    }

    companion object : KLogging()
}