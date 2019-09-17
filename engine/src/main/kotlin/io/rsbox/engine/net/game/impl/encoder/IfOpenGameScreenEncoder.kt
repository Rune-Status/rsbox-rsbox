package io.rsbox.engine.net.game.impl.encoder

import io.rsbox.engine.net.game.MessageEncoder
import io.rsbox.engine.net.game.impl.message.IfOpenGameScreenMessage
import io.rsbox.engine.net.game.packet.*

/**
 * @author Kyle Escobar
 */

class IfOpenGameScreenEncoder : MessageEncoder<IfOpenGameScreenMessage> {

    override fun encode(message: IfOpenGameScreenMessage, packet: GamePacketBuilder): GamePacket {
        packet.put(
            type = DataType.SHORT,
            order = DataOrder.LITTLE,
            transformation = DataTransformation.ADD,
            value = message.component
        )

        return packet.toGamePacket()
    }

}