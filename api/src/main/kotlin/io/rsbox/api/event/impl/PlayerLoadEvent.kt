package io.rsbox.api.event.impl

import io.rsbox.api.entity.Player
import io.rsbox.api.event.Event

/**
 * @author Kyle Escobar
 */

class PlayerLoadEvent(val player: Player) : Event() {
}