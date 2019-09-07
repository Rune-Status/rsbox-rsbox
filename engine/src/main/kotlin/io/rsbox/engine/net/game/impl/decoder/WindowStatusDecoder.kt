package io.rsbox.engine.net.game.impl.decoder

import io.rsbox.engine.net.game.MessageDecoder
import io.rsbox.engine.net.game.impl.message.WindowStatusMessage
import io.rsbox.engine.net.game.packet.GamePacketReader

/**
 * @author Graviton #1697
 */

class WindowStatusDecoder : MessageDecoder<WindowStatusMessage> {

    override fun decode(packet: GamePacketReader): WindowStatusMessage {
        return WindowStatusMessage(0,0,0)
    }

}