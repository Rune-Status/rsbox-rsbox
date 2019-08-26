package io.rsbox.server.net.session

import io.netty.channel.Channel
import io.rsbox.net.Message
import io.rsbox.net.session.Session
import io.rsbox.server.net.protocol.ProtocolProvider
import io.rsbox.server.net.protocol.RSProtocol
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class GameSession(val channel: Channel) : Session {

    val protocolProvider = ProtocolProvider()

    val protocol: RSProtocol? = null

    var ready: Boolean = false

    override fun send(message: Message) {

    }

    override fun <T : Message> messageReceived(message: T) {

    }

    override fun disconnect() {

    }

    override fun onReady() {
        ready = true
    }

    override fun onDisconnect() {

    }

    override fun onError(cause: Throwable) {
        logger.error("An error occurred in session. $cause")
    }

    companion object : KLogging()
}