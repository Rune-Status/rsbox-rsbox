package io.rsbox.net

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.login.GameLoginRequest
import io.rsbox.net.pipeline.RSChannelInitializer
import io.rsbox.net.protocol.ProtocolProvider
import io.rsbox.net.session.Session
import io.rsbox.net.session.SessionRegistry
import mu.KLogging
import net.runelite.cache.fs.Store
import java.math.BigInteger
import java.net.InetSocketAddress

/**
 * @author Kyle Escobar
 */

class NetworkServer {

    private val sessions = SessionRegistry()

    private val bossGroup = NioEventLoopGroup(Conf.SERVER[ServerSpec.network_threads])
    private val workerGroup = NioEventLoopGroup(1)

    private val bootstrap = ServerBootstrap()

    init {
        PROTOCOLS = ProtocolProvider()
    }

    fun start() {
        bootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(RSChannelInitializer(this))

        val address = InetSocketAddress(
            Conf.SERVER[ServerSpec.network_address],
            Conf.SERVER[ServerSpec.network_port]
        )

        bootstrap.bind(address).addListener { future ->
            if(future.isSuccess) {
                onBindSuccess(address)
            } else {
                onBindFailure(address, future.cause())
            }
        }
    }

    fun stop() {
        logger.info("Server networking shutdown signal received.")
        bootstrap.config().group().shutdownGracefully()
        bootstrap.config().childGroup().shutdownGracefully()
        logger.info("Server networking has stopped.")
    }

    private fun onBindSuccess(address: InetSocketAddress) {
        logger.info("Server networking listening on ${address.hostString}:${address.port}.")
    }

    private fun onBindFailure(address: InetSocketAddress, cause: Throwable) {
        logger.error("Unable to bind networking to ${address.hostString}:${address.port} due to $cause.")
    }

    fun newSession(ctx: ChannelHandlerContext): Session {
        val session = Session(this, ctx)
        sessions.add(session)
        return session
    }

    fun invalidateSession(session: Session) {
        sessions.remove(session)
    }

    fun getLoginRequestSession(request: GameLoginRequest): Session? {
        sessions.sessions.forEach { s ->
            if(s.key.lastLoginRequest != null) {
                if(s.key.lastLoginRequest!!.seed == request.seed) return s.key
            }
        }
        return null
    }

    companion object : KLogging() {
        lateinit var PROTOCOLS: ProtocolProvider
        var revision: Int = -1
        lateinit var cacheStore: Store
        lateinit var exponent: BigInteger
        lateinit var modulus: BigInteger
    }
}