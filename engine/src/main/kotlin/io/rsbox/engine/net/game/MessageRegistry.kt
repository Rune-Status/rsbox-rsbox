package io.rsbox.engine.net.game

import mu.KLogging

/**
 * @author Kyle Escobar
 */

class MessageRegistry {

    private val messages = hashMapOf<Int, Class<out Message>>()

    fun <M : Message> bind(opcode: Int, messageClass: Class<M>) {
        if(messages.containsKey(opcode)) {
            logger.error("Unable to bind opcode $opcode as it already is bound.")
            return
        }

        messages[opcode] = messageClass
    }

    fun find(opcode: Int): Class<out Message>? {
        return messages[opcode]
    }

    fun find(messageClass: Class<out Message>): Int {
        val reversed = messages.entries.associate { it.value to it.key }
        if(!reversed.containsKey(messageClass)) {
            return -1
        }
        return reversed.getValue(messageClass)
    }

    fun count(): Int = messages.size

    companion object : KLogging()
}