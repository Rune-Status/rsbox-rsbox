package io.rsbox.net.registry

import io.rsbox.net.Message
import io.rsbox.net.MessageHandler

/**
 * @author Kyle Escobar
 */

class HandlerRegistry {

    private val handlers = hashMapOf<Class<out Message>, MessageHandler<*, *>>()

    fun <M : Message, H : MessageHandler<*, in M>> bind(messageClass: Class<M>, handlerClass: Class<H>) {
        val handler: MessageHandler<*, in M> = handlerClass.newInstance()
        handlers[messageClass] = handler
    }

    @Suppress("UNCHECKED_CAST")
    fun <M : Message> find(messageClass: Class<M>): MessageHandler<*, M> {
        return handlers[messageClass] as MessageHandler<*, M>
    }

}