package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.model.GamePacket

/**
 * @author Kyle Escobar
 */

class RSPacketCodec(session: Session) : MessageToMessageCodec<ByteBuf, GamePacket>() {

    override fun encode(ctx: ChannelHandlerContext, msg: GamePacket, out: MutableList<Any>) {

    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {

    }

}