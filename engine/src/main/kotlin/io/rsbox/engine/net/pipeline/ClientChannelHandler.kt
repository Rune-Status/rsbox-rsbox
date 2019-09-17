package io.rsbox.engine.net.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.rsbox.engine.net.NetworkServer
import io.rsbox.engine.net.Session
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Kyle Escobar
 */

class ClientChannelHandler(private val networkServer: NetworkServer) : ChannelInboundHandlerAdapter() {

    private val session = AtomicReference<Session>(null)

    override fun channelActive(ctx: ChannelHandlerContext) {
        val s = Session(ctx, networkServer)
        session.compareAndSet(null, s)
        session.get().onConnect()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        session.get().onDisconnect()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        session.get().onMessageReceived(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        session.get().onError(cause)
    }

}