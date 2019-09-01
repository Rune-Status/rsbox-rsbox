package io.rsbox.engine.net

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.Engine
import io.rsbox.engine.net.pipeline.ClientChannelInitializer
import mu.KLogging
import java.net.InetSocketAddress

/**
 * @author Kyle Escobar
 */

class NetworkServer(val engine: Engine) {

    private val bossGroup = NioEventLoopGroup(Conf.SERVER[ServerSpec.network_threads])
    private val workerGroup = NioEventLoopGroup(1)

    private val bootstrap = ServerBootstrap()

    fun start() {
        logger.info("Starting engine networking.")

        bootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(ClientChannelInitializer(this))

        val address = Conf.SERVER[ServerSpec.network_address]
        val port = Conf.SERVER[ServerSpec.network_port]

        val socketAddr = InetSocketAddress(address, port)

        bootstrap.bind(socketAddr).addListener { future ->
            if(future.isSuccess) {
                this.onBindSuccess(socketAddr)
            } else {
                this.onBindFailure(socketAddr, future.cause())
            }
        }
    }

    fun shutdown() {
        logger.info("Server network shutdown signal received...")
        bootstrap.config().group().shutdownGracefully()
        bootstrap.config().childGroup().shutdownGracefully()
        logger.info("Server network has been shutdown.")
    }

    private fun onBindSuccess(address: InetSocketAddress) {
        logger.info("Server network is listening for connections on ${address.hostString}:${address.port}...")
    }

    private fun onBindFailure(address: InetSocketAddress, cause: Throwable) {
        logger.error("Unable to bind server network to ${address.hostString}:${address.port}. Cause: $cause")
        System.exit(0)
    }

    companion object : KLogging()
}