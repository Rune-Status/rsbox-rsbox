package io.rsbox.util.boxhash

import io.rsbox.util.boxhash.BoxHasher.INITIAL_V0
import io.rsbox.util.boxhash.BoxHasher.INITIAL_V1
import io.rsbox.util.boxhash.BoxHasher.INITIAL_V2
import io.rsbox.util.boxhash.BoxHasher.INITIAL_V3
import io.rsbox.util.boxhash.BoxHasher.bytesToLong
import io.rsbox.util.boxhash.BoxHasher.rotateLeft

/**
 * @author Kyle Escobar
 */

class BoxHasherStream
/**
 * Initializes a streaming digest using a key and compression rounds.
 *
 * @param key
 * the key to use to seed this hash container.
 * @param c
 * the desired rounds of C compression.
 * @param d
 * the desired rounds of D compression.
 */
internal constructor(
    key: ByteArray,
    /**
     * The specified rounds of C compression.
     */
    private val c: Int,
    /**
     * The specified rounds of D compression.
     */
    private val d: Int
) {

    /**
     * Counter to keep track of the input
     */
    private var len: Byte = 0

    /**
     * Index to keep track of chunk positioning.
     */
    private var m_idx: Int = 0

    /**
     * The current value for the m number.
     */
    private var m: Long = 0

    /**
     * The current value for the this.v0 number.
     */
    private var v0: Long = 0

    /**
     * The current value for the this.v1 number.
     */
    private var v1: Long = 0

    /**
     * The current value for the this.v2 number.
     */
    private var v2: Long = 0

    /**
     * The current value for the this.v3 number.
     */
    private var v3: Long = 0

    init {
        if (key.size != 16) {
            throw IllegalArgumentException("Key must be exactly 16 bytes!")
        }

        val k0 = bytesToLong(key, 0)
        val k1 = bytesToLong(key, 8)

        this.v0 = INITIAL_V0 xor k0
        this.v1 = INITIAL_V1 xor k1
        this.v2 = INITIAL_V2 xor k0
        this.v3 = INITIAL_V3 xor k1

        this.m = 0
        this.len = 0
        this.m_idx = 0
    }

    /**
     * Updates the hash with a single byte.
     *
     * This will only modify the internal `m` value, nothing will be modified
     * in the actual `v*` states until an 8-byte block has been provided.
     *
     * @param b
     * the byte being added to the digest.
     * @return
     * the same [SipHasherStream] for chaining.
     */
    fun update(b: Byte): BoxHasherStream {
        this.len++
        this.m = this.m or (b.toLong() and 0xff shl this.m_idx++ * 8)
        if (this.m_idx < 8) {
            return this
        }
        this.v3 = this.v3 xor this.m
        for (i in 0 until this.c) {
            round()
        }
        this.v0 = this.v0 xor this.m
        this.m_idx = 0
        this.m = 0
        return this
    }

    /**
     * Updates the hash with an array of bytes.
     *
     * @param bytes
     * the bytes being added to the digest.
     * @return
     * the same [SipHasherStream] for chaining.
     */
    fun update(bytes: ByteArray): BoxHasherStream {
        for (b in bytes) {
            update(b)
        }
        return this
    }

    /**
     * Finalizes the digest and returns the hash.
     *
     * This works by padding to the next 8-byte block, before applying
     * the compression rounds once more - but this time using D rounds
     * of compression rather than C.
     *
     * @return
     * the final result of the hash as a long.
     */
    fun digest(): Long {
        val msgLenMod256 = this.len

        while (this.m_idx < 7) {
            update(0.toByte())
        }
        update(msgLenMod256)

        this.v2 = this.v2 xor 0xff
        for (i in 0 until this.d) {
            round()
        }

        return this.v0 xor this.v1 xor this.v2 xor this.v3
    }

    /**
     * SipRound implementation for internal use.
     */
    private fun round() {
        this.v0 += this.v1
        this.v2 += this.v3
        this.v1 = rotateLeft(this.v1, 13)
        this.v3 = rotateLeft(this.v3, 16)

        this.v1 = this.v1 xor this.v0
        this.v3 = this.v3 xor this.v2
        this.v0 = rotateLeft(this.v0, 32)

        this.v2 += this.v1
        this.v0 += this.v3
        this.v1 = rotateLeft(this.v1, 17)
        this.v3 = rotateLeft(this.v3, 21)

        this.v1 = this.v1 xor this.v2
        this.v3 = this.v3 xor this.v0
        this.v2 = rotateLeft(this.v2, 32)
    }
}