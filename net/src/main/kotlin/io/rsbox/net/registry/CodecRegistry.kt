package io.rsbox.net.registry

import io.rsbox.net.Codec
import io.rsbox.net.Message
import io.rsbox.net.exception.IllegalOpcodeException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.reflect.Constructor
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Kyle Escobar
 */

class CodecRegistry {

    private val messages = ConcurrentHashMap<Class<out Message>, Codec.CodecRegistration>()

    private val opcodes = ConcurrentHashMap<Int, Codec<*, *>>()

    fun <M : Message, C : Codec<in M, *>> bindInbound(messageClass: Class<M>, codecClass: Class<C>, opcode: Int) {
        val codec: C
        try {
            val con: Constructor<C> = codecClass.getConstructor()
            codec = con.newInstance()
        } catch(e : Exception) {
            throw IllegalArgumentException("Codec could not be created!", e)
        }

        if(opcodes.containsKey(opcode)) {
            throw IllegalStateException("Failure to bind opcode $opcode as it already is bound.")
        }

        put(opcode, codec)

        val reg = Codec.CodecRegistration(opcode, codec)
        messages[messageClass] = reg
    }

    fun <M : Message, C : Codec<*, in M>> bindOutbound(messageClass: Class<M>, codecClass: Class<C>, opcode: Int) {
        val codec: C
        try {
            val con: Constructor<C> = codecClass.getConstructor()
            codec = con.newInstance()
        } catch(e : Exception) {
            throw IllegalArgumentException("Codec could not be created!", e)
        }

        if(opcodes.containsKey(opcode)) {
            throw IllegalStateException("Failure to bind opcode $opcode as it already is bound.")
        }

        put(opcode, codec)

        val reg = Codec.CodecRegistration(opcode, codec)
        messages[messageClass] = reg
    }

    private fun get(opcode: Int): Codec<*, *>? {
        return opcodes[opcode]
    }

    private fun put(opcode: Int, codec: Codec<*, *>) {
        if(get(opcode) != null) {
            throw IllegalStateException("Codec has already been bound to an opcode.")
        }
        opcodes[opcode] = codec
    }

    fun find(opcode: Int): Codec<*, *> {
        try {
            return get(opcode) ?: throw NullPointerException()
        } catch(e : Exception) {
            throw IllegalOpcodeException("Opcode $opcode is not bound!")
        }
    }

    fun <M : Message> find(messageClass: Class<M>): Codec.CodecRegistration {
        try {
            return messages[messageClass] ?: throw NullPointerException()
        } catch(e : Exception) {
            throw IllegalOpcodeException("Message class ${messageClass.simpleName} is not bound!")
        }
    }
}