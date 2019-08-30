package io.rsbox.util.boxhash

import io.rsbox.util.boxhash.BoxHasher.DEFAULT_C
import io.rsbox.util.boxhash.BoxHasher.DEFAULT_D
import io.rsbox.util.boxhash.BoxHasher.INITIAL_V0
import io.rsbox.util.boxhash.BoxHasher.INITIAL_V1
import io.rsbox.util.boxhash.BoxHasher.INITIAL_V2
import io.rsbox.util.boxhash.BoxHasher.INITIAL_V3
import io.rsbox.util.boxhash.BoxHasher.bytesToLong

/**
 * @author Kyle Escobar
 */

class BoxHasherContainer
/**
 * Initializes a container from a key seed.
 *
 * @param key
 * the key to use to seed this hash container.
 */
internal constructor(key: ByteArray) {

    /**
     * The seeded value for the magic v0 number.
     */
    private val v0: Long

    /**
     * The seeded value for the magic v1 number.
     */
    private val v1: Long

    /**
     * The seeded value for the magic v2 number.
     */
    private val v2: Long

    /**
     * The seeded value for the magic v3 number.
     */
    private val v3: Long

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
    }

    /**
     * Hashes input data using the preconfigured state.
     *
     * @param data
     * the data to hash and digest.
     * @param c
     * the desired rounds of C compression.
     * @param d
     * the desired rounds of D compression.
     * @return
     * a long value as the output of the hash.
     */
    @JvmOverloads
    fun hash(data: ByteArray, c: Int = DEFAULT_C, d: Int = DEFAULT_D): Long {
        return BoxHasher.hash(
            c, d,
            this.v0,
            this.v1,
            this.v2,
            this.v3,
            data
        )
    }
}
/**
 * Hashes input data using the preconfigured state.
 *
 * @param data
 * the data to hash and digest.
 * @return
 * a long value as the output of the hash.
 */