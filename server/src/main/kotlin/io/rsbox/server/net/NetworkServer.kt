package io.rsbox.server.net

import io.netty.channel.ChannelFuture
import io.rsbox.server.Server
import java.net.InetSocketAddress

/**
 * @author Kyle Escobar
 */

/**
 * Represents a networked server.
 *
 * @param server The associated server this network server is for.
 */
abstract class NetworkServer(open val server: Server) {

    abstract fun bind(address: InetSocketAddress): ChannelFuture

    abstract fun onBindSuccess(address: InetSocketAddress)

    abstract fun onBindFailure(address: InetSocketAddress, cause: Throwable)
}