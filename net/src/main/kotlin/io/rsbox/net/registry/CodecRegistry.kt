package io.rsbox.net.registry

import io.rsbox.net.Codec
import io.rsbox.net.Message
import io.rsbox.net.exceptions.IllegalOpcodeException
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Kyle Escobar
 */

/**
 * Stores tables for opcode, codec, and message associations.
 *
 * @param size The total number of opcodes. (The highest opcode + 1)
 */
class CodecRegistry(private val size: Int) {

    private val messages = ConcurrentHashMap<Class<out Message>, Codec.CodecRegistration>()

    private val opcodes = ConcurrentHashMap<Int, Codec<*>>()

    /**
     * Associates an opcode, message class, and codec class to each other.
     *
     * @param opcode The opcode integer
     * @param message The association message class
     * @param codec The association codec class.
     */
    fun <M : Message, C : Codec<in M>> bind(opcode: Int, message: Class<M>, codec: Class<C>) {
        val codecInst: C
        try {
            val con = codec.getConstructor()
            codecInst = con.newInstance()
        } catch(e : Exception) {
            throw Exception("Codec instance could not be created.", e)
        }
        put(opcode, codecInst)
        val reg = Codec.CodecRegistration(opcode, codecInst)
        messages[message] = reg
    }

    private fun get(opcode: Int): Codec<*>? {
        return opcodes[opcode]
    }

    private fun put(opcode: Int, codec: Codec<*>) {
        if(get(opcode) != null) {
            throw Exception("Opcode $opcode is already in use.")
        }
        opcodes[opcode] = codec
    }

    /**
     * Retrieves a codec from the table
     *
     * @param opcode The opcode to retrieve for.
     * @return The associated codec.
     */
    fun find(opcode: Int): Codec<*> {
        try {
            return get(opcode) ?: throw Exception()
        } catch (e : Exception) {
            throw IllegalOpcodeException("Opcode $opcode is not bound!")
        }
    }

    /**
     * Retrieves a codec registration for a given message class.
     *
     * @param message The message class to retrieve for.
     * @return The Codec registration object.
     */
    fun <M : Message> find(message: Class<M>): Codec.CodecRegistration {
        return messages[message] ?: throw IllegalArgumentException("Message ${message.simpleName} does not have a codec associated.")
    }
}