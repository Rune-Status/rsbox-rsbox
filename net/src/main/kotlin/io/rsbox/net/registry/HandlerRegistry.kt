package io.rsbox.net.registry

import io.rsbox.net.Message
import io.rsbox.net.MessageHandler

/**
 * @author Kyle Escobar
 */

/**
 * Stores a map of message handlers and their associated message class.
 */
class HandlerRegistry {

    private val handlers = hashMapOf<Class<out Message>, MessageHandler<*, *>>()

    /**
     * Associates a message class to a handler class.
     *
     * @param message The message class.
     * @param handler The handler class.
     */
    fun <M : Message, H : MessageHandler<*, in M>> bind(message: Class<M>, handler: Class<H>) {
        val h = handler.newInstance()
        handlers[message] = h
    }

    /**
     * Retrieves a handler given a message class.
     *
     * @param message The message class to retrieve for.
     * @return The associated message handler.
     */
    @Suppress("UNCHECKED_CAST")
    fun <M : Message> find(message: Class<M>): MessageHandler<*, M> {
        return handlers[message] as MessageHandler<*, M>
    }
}