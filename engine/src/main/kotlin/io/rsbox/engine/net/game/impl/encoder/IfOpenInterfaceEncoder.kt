package io.rsbox.engine.net.game.impl.encoder

import io.rsbox.engine.net.game.MessageEncoder
import io.rsbox.engine.net.game.impl.message.IfOpenInterfaceMessage
import io.rsbox.engine.net.game.packet.*

/**
 * @author Kyle Escobar
 */

class IfOpenInterfaceEncoder : MessageEncoder<IfOpenInterfaceMessage> {

    override fun encode(message: IfOpenInterfaceMessage, packet: GamePacketBuilder): GamePacket {
        // INTERFACE TYPE
        packet.put(
            type = DataType.BYTE,
            transformation = DataTransformation.ADD,
            value = message.type
        )

        // INTERFACE ID
        packet.put(
            type = DataType.INT,
            order = DataOrder.MIDDLE,
            value = (message.parent shl 16) or message.child
        )

        // COMPONENT
        packet.put(
            type = DataType.SHORT,
            order = DataOrder.LITTLE,
            transformation = DataTransformation.ADD,
            value = message.component
        )

        return packet.toGamePacket()
    }

}