package io.rsbox.engine.net.game.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.rsbox.engine.net.game.packet.PacketType
import io.rsbox.util.IsaacRandom

/**
 * @author Kyle Escobar
 */

class GamePacketDecoder(private val random: IsaacRandom) : ByteToMessageDecoder() {

    private var state = State.OPCODE

    private var opcode: Int = -1

    private var length: Int = 0

    private var type = PacketType.FIXED

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        when(state) {
            State.OPCODE -> decodeOpcode(ctx, buf, out)
            State.LENGTH -> decodeLength(ctx, buf, out)
            State.PAYLOAD -> decodePayload(ctx, buf, out)
        }
    }

    private fun decodeOpcode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.isReadable) {
            opcode = buf.readUnsignedByte().toInt() - random.nextInt() and 0xFF

        }
    }

    private fun decodeLength(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {

    }

    private fun decodePayload(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {

    }

    private enum class State {
        OPCODE,
        LENGTH,
        PAYLOAD;
    }
}