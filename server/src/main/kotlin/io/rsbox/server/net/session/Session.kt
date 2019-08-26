package io.rsbox.server.net.session

import io.netty.channel.ChannelFuture
import io.rsbox.server.net.Message

/**
 * @author Kyle Escobar
 */

/**
 * Represents a connection to a server.
 */
interface Session {

    /**
     * Called once the session is ready.
     */
    fun onReady()

    /**
     * Called after the session has been disconnected.
     */
    fun onDisconnect()

    /**
     * Closes the session.
     */
    fun disconnect()

    /**
     * Sends a message across the network.
     */
    fun send(message: Message): ChannelFuture?

    /**
     * Called when a message is received from a client across the network.
     *
     * @param message The message that is received.
     */
    fun <T : Message> messageReceived(message: T)

    fun onError(cause: Throwable)
}