package io.rsbox.api.event

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

/**
 * @author Kyle Escobar
 */

object EventManager {

    private val listeners = Object2ObjectOpenHashMap<Class<out Event>, (Event) -> Unit>()

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> on(event: Class<E>, logic: (E) -> Unit) {
        this.listeners[event] = logic as (Event) -> Unit
    }

    fun <E : Event> trigger(event: E, eventLogic: (E) -> Unit): E {
        val logics = this.listeners.filter { it.key == event::class.java }

        if(logics.isEmpty()) {
            eventLogic(event)
            return event
        }

        logics.forEach { _, logic ->
            logic(event)

            if(event is Cancellable) {
                if(!event.isCancelled()) {
                    eventLogic(event)
                } else {
                    return@forEach
                }
            }
        }
        return event
    }

}