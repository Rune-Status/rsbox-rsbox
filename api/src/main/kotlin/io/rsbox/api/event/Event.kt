package io.rsbox.api.event

/**
 * @author Kyle Escobar
 */

abstract class Event {

    companion object {
        fun <E : Event> on(event: Class<E>, logic: (E) -> Unit) = EventManager.on(event, logic)
        fun <E : Event> trigger(event: E, eventLogic: (E) -> Unit) = EventManager.trigger(event, eventLogic)
    }
}