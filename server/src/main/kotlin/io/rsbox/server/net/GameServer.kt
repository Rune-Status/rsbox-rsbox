package io.rsbox.server.net

import io.netty.channel.Channel
import io.rsbox.net.ConnectionManager
import io.rsbox.net.session.Session
import io.rsbox.server.Server
import mu.KLogging
import java.net.InetSocketAddress

/**
 * @author Kyle Escobar
 */

/**
 * The main class which handles all the networking for the server.
 *
 * @param server The associated server to handle networking for.
 */
class GameServer(override val server: Server) : SocketServer(server), ConnectionManager {

    init {
        bootstrap.childHandler(null)
    }

    /**
     * Sends the server the shutdown signal.
     */
    override fun shutdown() {
        logger.info("Server networking shutdown signal received.")

        bootstrap.config().group().shutdownGracefully()
        bootstrap.config().childGroup().shutdownGracefully()

        logger.info("Server networking has been shutdown.")
    }

    /**
     * Called when binding to an address succeeds.
     *
     * @param address The address that was bound.
     */
    override fun onBindSuccess(address: InetSocketAddress) {
        logger.info("Server is listening for connections on ${address.hostString}:${address.port}.")
    }

    /**
     * Called when binding to an address fails.
     *
     * @param address The address that was bound.
     * @param cause A throwable which indicates why the bind failed.
     */
    override fun onBindFailure(address: InetSocketAddress, cause: Throwable) {
        logger.error("Server failed to bind to ${address.hostString}:${address.port} with cause $cause.")
        System.exit(-1)
    }

    override fun newSession(channel: Channel): Session {
        TODO("Not yet implemented.")
    }

    override fun sessionInvalidated(session: Session) {
        TODO("Not yet implemented.")
    }

    companion object : KLogging()
}