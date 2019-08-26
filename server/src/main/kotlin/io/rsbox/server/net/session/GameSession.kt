package io.rsbox.server.net.session

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.rsbox.server.net.Message
import io.rsbox.server.net.MessageHandler
import io.rsbox.server.net.pipeline.CodecChannelHandler
import io.rsbox.server.net.protocol.ProtocolProvider
import io.rsbox.server.net.protocol.RSProtocol
import mu.KLogging

/**
 * @author Kyle Escobar
 */

/**
 * Defines a connection session for the game.
 *
 * @param channel The channel this session uses.
 */
class GameSession(val channel: Channel) : Session {

    val protocolProvider = ProtocolProvider()

    /**
     * This is the current protocol logic the session is using.
     */
    var protocol: RSProtocol = protocolProvider.handshake

    var ready: Boolean = false

    /**
     * Sends the message over the network.
     * If the message is an instance of AsyncMessage,
     * The message will be queued and sent next game tick.
     *
     * @param message The message to be sent.
     */
    override fun send(message: Message): ChannelFuture? {
        // TODO Add async messages that only send the queue per game tick for each session.
        return channel.writeAndFlush(message)
    }

    /**
     * Executed when the session receives a message.
     *
     * @param message The message that was sent.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : Message> messageReceived(message: T) {
        val handler = protocol.getMessageHandler(message::class.java) as MessageHandler<GameSession, Message>
        handler.handle(this, message)
    }

    /**
     * Closes the session and channels.
     */
    override fun disconnect() {

    }

    /**
     * Executed once the session has been initialized.
     */
    override fun onReady() {
        val p = channel.pipeline()
        p.addBefore("handler", "codec_handler", CodecChannelHandler(this))

        ready = true
    }

    /**
     * Executed after the session or channel gets disconnected or goes inactive.
     */
    override fun onDisconnect() {

    }

    /**
     * Executed whenever there is an error in the network thread.
     *
     * @param cause The throwable cause of the error. Provides some additional debug information.
     */
    override fun onError(cause: Throwable) {
        logger.error("An error occurred in session: ${cause.message}")
    }

    companion object : KLogging()
}