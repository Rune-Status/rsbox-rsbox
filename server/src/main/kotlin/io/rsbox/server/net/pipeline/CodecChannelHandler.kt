package io.rsbox.server.net.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.rsbox.net.Message
import io.rsbox.server.net.session.GameSession

/**
 * @author Kyle Escobar
 */

class CodecChannelHandler(private val session: GameSession) : MessageToMessageCodec<ByteBuf, Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: MutableList<Any>) {
        val codec = session.protocol.getOutboundCodec(msg.javaClass)
        val buf = codec.encode(ctx.alloc().buffer(), msg)
        out.add(buf)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val codec = session.protocol.getInboundCodec(msg)
        val message = codec.decode(msg)
        out.add(message)
    }

}