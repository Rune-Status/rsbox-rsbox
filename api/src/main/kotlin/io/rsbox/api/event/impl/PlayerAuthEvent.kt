package io.rsbox.api.event.impl

import io.rsbox.api.entity.Player
import io.rsbox.api.event.Cancellable
import io.rsbox.api.event.Event

/**
 * @author Kyle Escobar
 */

class PlayerAuthEvent(val player: Player) : Event(), Cancellable {

    /**
     * The login state response when this event is cancelled.
     * For ID's, see io.rsbox.engine.net.LoginState.
     */
    var loginStateId: Int = 13

    override var _cancelled: Boolean = false

    override fun cancel() {
        this._cancelled = true
    }
}