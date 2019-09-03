package io.rsbox.engine.net.game

import io.rsbox.engine.net.game.packet.GamePacket
import io.rsbox.engine.net.game.packet.GamePacketBuilder

/**
 * @author Kyle Escobar
 */

abstract class MessageEncoder<T : Message> {

    abstract fun encode(message: T, packet: GamePacketBuilder): GamePacket

}