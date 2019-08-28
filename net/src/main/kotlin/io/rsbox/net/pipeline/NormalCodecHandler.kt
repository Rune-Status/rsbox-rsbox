package io.rsbox.net.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.rsbox.net.Message
import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

class NormalCodecHandler(private val session: Session) : MessageToMessageCodec<ByteBuf, Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: MutableList<Any>) {
        val codec = session.protocol.getOutboundCodec(msg.javaClass)
        val buf = codec.encode(ctx.alloc().buffer(), msg)
        out.add(buf)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val opcode = session.protocol.readOpcode(msg)
        val codec = session.protocol.getInboundCodec(opcode)
        val message = codec.decode(msg)
        out.add(message)
    }

}