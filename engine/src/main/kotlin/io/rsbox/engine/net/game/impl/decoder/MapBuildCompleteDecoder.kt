package io.rsbox.engine.net.game.impl.decoder

import io.rsbox.engine.net.game.MessageDecoder
import io.rsbox.engine.net.game.impl.message.MapBuildCompleteMessage
import io.rsbox.engine.net.game.packet.GamePacketReader

/**
 * @author Graviton #1697
 */

class MapBuildCompleteDecoder : MessageDecoder<MapBuildCompleteMessage> {

    override fun decode(packet: GamePacketReader): MapBuildCompleteMessage {
        return MapBuildCompleteMessage()
    }

}