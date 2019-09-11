package io.rsbox.api.event

/**
 * @author Kyle Escobar
 */

abstract class Event {

    private var cancelled: Boolean = false

    open fun cancel() {
        this.cancelled = true
    }

    fun isCancelled(): Boolean = cancelled
}