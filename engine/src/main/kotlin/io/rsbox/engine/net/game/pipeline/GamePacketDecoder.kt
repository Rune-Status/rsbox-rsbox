package io.rsbox.engine.net.game.pipeline

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.rsbox.engine.net.game.RSProtocol
import io.rsbox.engine.net.game.packet.ClientPackets
import io.rsbox.engine.net.game.packet.GamePacket
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

    private var ignored = false

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        when(state) {
            State.OPCODE -> decodeOpcode(buf, out)
            State.LENGTH -> decodeLength(buf, out)
            State.PAYLOAD -> decodePayload(buf, out)
        }
    }

    private fun decodeOpcode(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.isReadable) {
            opcode = buf.readUnsignedByte().toInt() - random.nextInt() and 0xFF

            if(!RSProtocol.isInboundOpcode(opcode)) {
                RSProtocol.logger.warn("Received opcode $opcode which has not been bound to a message.")
                ignored = true
            }

            length = ClientPackets.opcodeMap[opcode]?.length ?: throw Exception("Unable to find length of opcode $opcode.")

            type = when(length) {
                -1 -> PacketType.VARIABLE_BYTE
                -2 -> PacketType.VARIABLE_SHORT
                else -> PacketType.FIXED
            }

            if(length != 0) {
                state = if(type == PacketType.VARIABLE_BYTE || type == PacketType.VARIABLE_SHORT) {
                    State.LENGTH
                } else {
                    State.PAYLOAD
                }
            } else {
                out.add(GamePacket(opcode, type, Unpooled.EMPTY_BUFFER))
            }
        }
    }

    private fun decodeLength(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.isReadable) {
            length = if(type == PacketType.VARIABLE_BYTE) buf.readUnsignedShort() else buf.readUnsignedByte().toInt()
            state = if(length != 0) {
                State.PAYLOAD
            } else {
                out.add(GamePacket(opcode, type, Unpooled.EMPTY_BUFFER))
                State.OPCODE
            }
        }
    }

    private fun decodePayload(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= length) {
            val payload = buf.readBytes(length)

            if(ignored) {
                out.add(GamePacket(255, type, payload))
                state = State.OPCODE
                return
            }

            out.add(GamePacket(opcode, type, payload))

            state = State.OPCODE
        }
    }

    private enum class State {
        OPCODE,
        LENGTH,
        PAYLOAD;
    }
}