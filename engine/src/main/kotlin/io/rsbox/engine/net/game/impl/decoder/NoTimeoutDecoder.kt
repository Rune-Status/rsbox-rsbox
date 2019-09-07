package io.rsbox.engine.net.game.impl.decoder

import io.rsbox.engine.net.game.MessageDecoder
import io.rsbox.engine.net.game.impl.message.NoTimeoutMessage
import io.rsbox.engine.net.game.packet.GamePacketReader

/**
 * @author Graviton #1697
 */

class NoTimeoutDecoder : MessageDecoder<NoTimeoutMessage> {

    override fun decode(packet: GamePacketReader): NoTimeoutMessage {
        return NoTimeoutMessage()
    }

}