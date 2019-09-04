package io.rsbox.engine.net.game

import io.rsbox.engine.net.game.packet.GamePacketReader

/**
 * @author Kyle Escobar
 */

interface MessageDecoder<T : Message> {

    fun decode(packet: GamePacketReader): T

}