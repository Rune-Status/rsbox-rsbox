package io.rsbox.engine.net.game.impl.decoder

import io.rsbox.engine.net.game.MessageDecoder
import io.rsbox.engine.net.game.impl.message.IgnoredMessage
import io.rsbox.engine.net.game.packet.GamePacketReader

/**
 * @author Kyle Escobar
 */

class IgnoredDecoder : MessageDecoder<IgnoredMessage> {

    override fun decode(packet: GamePacketReader): IgnoredMessage {
        return IgnoredMessage()
    }

}