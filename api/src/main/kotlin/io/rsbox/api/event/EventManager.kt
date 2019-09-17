package io.rsbox.api.event

import kotlin.reflect.KClass

/**
 * @author Kyle Escobar
 */

object EventManager {

    private val listeners = mutableMapOf<KClass<out Event>, (Event) -> Unit>()

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> registerListener(event: KClass<E>, logic: (E) -> Unit) {
        listeners[event] = logic as (Event) -> Unit
    }

    fun <E : Event> trigger(event: E, logic: (E) -> Unit, cancelLogic: (E) -> Unit = {}) {
        /**
         * Execute all listeners.
         */
        listeners.filter { it.key == event::class }.forEach { _, listenerLogic ->
            listenerLogic(event)
        }

        when(event.isCancelled()) {
            false -> { logic(event) }
            true -> { cancelLogic(event) }
        }
    }
}