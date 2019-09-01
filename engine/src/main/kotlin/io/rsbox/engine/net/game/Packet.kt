package io.rsbox.engine.net.game

import io.rsbox.engine.net.game.model.GamePacketBuilder
import io.rsbox.engine.net.game.model.GamePacketReader
import io.rsbox.engine.net.game.model.PacketType

/**
 * @author Kyle Escobar
 */

abstract class Packet(val opcode: Int, val type: PacketType = PacketType.FIXED) {

    lateinit var packet: GamePacketBuilder

    lateinit var reader: GamePacketReader

    open fun encode() { throw Exception("Unable to encode message ${this.javaClass.simpleName}.") }

    open fun decode() { throw Exception("Unable to decode message ${this.javaClass.simpleName}.") }

    open fun handle() { throw Exception("Unable to handle message ${this.javaClass.simpleName}.") }

}