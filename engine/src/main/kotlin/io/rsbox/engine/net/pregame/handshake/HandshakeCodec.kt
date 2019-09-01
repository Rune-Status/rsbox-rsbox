package io.rsbox.engine.net.pregame.handshake

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.rsbox.engine.net.LoginState
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.pregame.js5.JS5Codec
import io.rsbox.engine.net.pregame.login.LoginCodec
import io.rsbox.engine.net.pregame.login.LoginRequestType
import mu.KLogging
import java.math.BigInteger

/**
 * @author Kyle Escobar
 */

class HandshakeCodec(private val session: Session) : MessageToMessageCodec<ByteBuf, LoginState>() {

    override fun encode(ctx: ChannelHandlerContext, msg: LoginState, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer(1)
        buf.writeByte(msg.id)
        out.add(buf)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if(!msg.isReadable) return

        val opcode = msg.readByte().toInt()
        val handshake = HandshakeType.values().firstOrNull { it.opcode == opcode }
        when(handshake) {

            HandshakeType.JS5 -> this.decodeJS5Handshake(ctx, msg)

            HandshakeType.LOGIN -> this.decodeLoginHandshake(ctx)

            else -> {
                msg.readBytes(msg.readableBytes())
                logger.warn("Unhandled handshake opcode $opcode.")
                return
            }
        }
    }

    private fun decodeJS5Handshake(ctx: ChannelHandlerContext, buf: ByteBuf) {
        if(buf.readableBytes() < 4) return
        val revision = buf.readInt()
        if(revision != session.networkServer.engine.revision) {
            logger.info("Login request for sessionid ${session.sessionId} rejected due to client revision mismatch.")
            ctx.writeAndFlush(LoginState.REVISION_MISMATCH).addListener(ChannelFutureListener.CLOSE)
        } else {
            ctx.channel().writeAndFlush(LoginState.ACCEPTABLE).addListener { future ->
                if(future.isSuccess) {
                    val p = ctx.pipeline()
                    p.remove("handshake_codec")
                    p.addBefore("handler", "js5_codec", JS5Codec())
                }
            }
        }
    }

    private fun decodeLoginHandshake(ctx: ChannelHandlerContext) {
        val p = ctx.pipeline()
        val seed = (Math.random() * Long.MAX_VALUE).toLong()

        session.seed = seed

        ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(LoginState.ACCEPTABLE.id))
        ctx.writeAndFlush(ctx.alloc().buffer(8).writeLong(seed))

        p.remove("handshake_codec")
        p.addBefore("handler", "login_codec", LoginCodec(session))
    }

    companion object : KLogging()
}