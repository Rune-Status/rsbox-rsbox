package io.rsbox.engine.net.game.packet

import com.google.common.base.MoreObjects
import io.netty.buffer.ByteBuf

/**
 * Represents a single packet used in the in-game protocol.
 *
 * @author Graham
 */
class GamePacket(val opcode: Int, val type: PacketType, val payload: ByteBuf) {

    /**
     * Gets the payload length.
     *
     * @return The payload length.
     */
    val length: Int = payload.readableBytes()

    override fun toString(): String = MoreObjects.toStringHelper(this).add("opcode", opcode).add("type", type).add("length", length).toString()
}