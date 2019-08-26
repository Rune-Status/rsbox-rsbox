package io.rsbox.server.net

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.server.Server
import io.rsbox.server.config.Conf
import io.rsbox.server.config.spec.ServerSpec
import java.net.InetSocketAddress

/**
 * @author Kyle Escobar
 */

abstract class SocketServer(override val server: Server) : NetworkServer(server) {

    private val bossGroup = NioEventLoopGroup(Conf.SERVER[ServerSpec.network_threads])
    private val workerGroup = NioEventLoopGroup(1)

    val bootstrap = ServerBootstrap()

    init {
        bootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
    }

    override fun bind(address: InetSocketAddress): ChannelFuture {
        return bootstrap.bind(address).addListener { future ->
            if(future.isSuccess) {
                onBindSuccess(address)
            } else {
                onBindFailure(address, future.cause())
            }
        }
    }
}