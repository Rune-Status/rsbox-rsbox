package io.rsbox.api.event.impl

import io.rsbox.api.LoginStateResponse
import io.rsbox.api.entity.Player
import io.rsbox.api.event.Event

/**
 * @author Kyle Escobar
 */

class PlayerAuthEvent(val player: Player) : Event() {
    var loginStateResponse: LoginStateResponse = LoginStateResponse.COULD_NOT_COMPLETE_LOGIN
}