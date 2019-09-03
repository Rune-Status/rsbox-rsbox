package io.rsbox.engine.net.game

import io.rsbox.engine.net.game.packet.GamePacketReader

/**
 * @author Kyle Escobar
 */

abstract class MessageDecoder<T : Message> {

    abstract fun decode(packet: GamePacketReader): T

}