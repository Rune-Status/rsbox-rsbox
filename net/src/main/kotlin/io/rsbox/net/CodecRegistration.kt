package io.rsbox.net

/**
 * @author Kyle Escobar
 */

/**
 * A registration class which defines an opcode associated with a codec.
 *
 * @param opcode The opcode integer.
 * @param codec The associated codec.
 */
data class CodecRegistration(val opcode: Int, val codec: Codec<*>)