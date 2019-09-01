package io.rsbox.engine.net.game

import mu.KLogging

/**
 * @author Kyle Escobar
 */

class PacketRegistry {

    private val messages = hashMapOf<Int, Class<out Packet>>()
    private val messageInstances = hashMapOf<Int, Packet>()

    fun <M : Packet> bind(opcode: Int, messageClass: Class<M>) {
        if(messages.containsKey(opcode)) {
            logger.error("Unable to bind opcode $opcode as it already is bound.")
            return
        }

        messages[opcode] = messageClass

        val con = messageClass.getConstructor()
        val inst = con.newInstance()
        messageInstances[opcode] = inst
    }

    fun find(opcode: Int): Class<out Packet>? {
        return messages[opcode]
    }

    fun find(messageClass: Class<out Packet>): Int {
        val reversed = messages.entries.associate { it.value to it.key }
        if(!reversed.containsKey(messageClass)) {
            return -1
        }
        return reversed.getValue(messageClass)
    }

    fun getMessage(opcode: Int): Packet? {
        return messageInstances[opcode]
    }

    fun count(): Int = messages.size

    companion object : KLogging()
}