package io.rsbox.net.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.rsbox.net.Message
import io.rsbox.net.NetworkServer
import io.rsbox.net.session.Session
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Kyle Escobar
 */

class RSChannelHandler(private val server: NetworkServer) : SimpleChannelInboundHandler<Message>() {

    private val session = AtomicReference<Session>(null)

    override fun channelActive(ctx: ChannelHandlerContext) {
        val s = server.newSession(ctx)
        session.compareAndSet(null, s)
        s.onReady()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        session.get().onDisconnect()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message) {
        session.get().onMessageReceive(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        session.get().onError(cause)
    }

}