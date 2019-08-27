package io.rsbox.server.net.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.rsbox.server.net.Message
import io.rsbox.server.net.GameServer
import io.rsbox.server.net.session.GameSession
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Kyle Escobar
 */

/**
 * This class handles incoming messages after they have been decoded by the message codec.
 *
 * @param gameServer The associated game server.
 */
class GameChannelHandler(private val gameServer: GameServer) : SimpleChannelInboundHandler<Message>() {

    private var session = AtomicReference<GameSession>(null)

    override fun channelActive(ctx: ChannelHandlerContext) {
        val channel = ctx.channel()
        val s = gameServer.newSession(channel) as GameSession
        if(!session.compareAndSet(null, s)) {
            throw IllegalStateException("Unable to bind session to channel multiple times.")
        }
        s.onReady()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        session.get().onDisconnect()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message) {
        session.get().messageReceived(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        session.get().onError(cause)
    }
}