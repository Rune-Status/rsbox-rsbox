package io.rsbox.util

/**
 * ------------------------------------------------------------------------------
 * Rand.java: By Bob Jenkins.  My random number generator, ISAAC.
 * rand.init() -- initialize
 * rand.val()  -- get a random value
 * MODIFIED:
 * 960327: Creation (addition of randinit, really)
 * 970719: use context, not global variables, for internal state
 * 980224: Translate to Java
 * ------------------------------------------------------------------------------
 */

class IsaacRandom/* equivalent to randinit(ctx, TRUE) after putting seed in randctx in C */(seed: IntArray) {
    var count: Int = 0                           /* count through the results in rsl[] */
    var rsl: IntArray                                /* the results given to the user */
    private var mem: IntArray? = null                                   /* the internal state */
    private var a: Int = 0                                              /* accumulator */
    private var b: Int = 0                                          /* the last result */
    private var c: Int = 0              /* counter, guarantees cycle is at least 2^^40 */


    init {
        mem = IntArray(SIZE)
        rsl = IntArray(SIZE)
        for (i in seed.indices) {
            rsl[i] = seed[i]
        }
        Init(true)
    }


    /* Generate 256 results.  This is a fast (not small) implementation. */
    fun Isaac() {
        var i = 0
        var j: Int = SIZE / 2
        var x: Int
        var y: Int

        b += ++c
        while (i < SIZE / 2) {
            x = mem!![i]
            a = a xor (a shl 13)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b

            x = mem!![i]
            a = a xor a.ushr(6)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b

            x = mem!![i]
            a = a xor (a shl 2)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b

            x = mem!![i]
            a = a xor a.ushr(16)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b
        }

        j = 0
        while (j < SIZE / 2) {
            x = mem!![i]
            a = a xor (a shl 13)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b

            x = mem!![i]
            a = a xor a.ushr(6)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b

            x = mem!![i]
            a = a xor (a shl 2)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b

            x = mem!![i]
            a = a xor a.ushr(16)
            a += mem!![j++]
            y = mem!![x and MASK shr 2] + a + b
            mem!![i] = y
            b = mem!![y shr SIZEL and MASK shr 2] + x
            rsl[i++] = b
        }
    }


    /* initialize, or reinitialize, this instance of rand */
    fun Init(flag: Boolean) {
        var i: Int
        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var e: Int
        var f: Int
        var g: Int
        var h: Int
        h = -0x61c88647
        g = h
        f = g
        e = f
        d = e
        c = d
        b = c
        a = b                        /* the golden ratio */

        i = 0
        while (i < 4) {
            a = a xor (b shl 11)
            d += a
            b += c
            b = b xor c.ushr(2)
            e += b
            c += d
            c = c xor (d shl 8)
            f += c
            d += e
            d = d xor e.ushr(16)
            g += d
            e += f
            e = e xor (f shl 10)
            h += e
            f += g
            f = f xor g.ushr(4)
            a += f
            g += h
            g = g xor (h shl 8)
            b += g
            h += a
            h = h xor a.ushr(9)
            c += h
            a += b
            ++i
        }

        i = 0
        while (i < SIZE) {              /* fill in mem[] with messy stuff */
            if (flag) {
                a += rsl[i]
                b += rsl[i + 1]
                c += rsl[i + 2]
                d += rsl[i + 3]
                e += rsl[i + 4]
                f += rsl[i + 5]
                g += rsl[i + 6]
                h += rsl[i + 7]
            }
            a = a xor (b shl 11)
            d += a
            b += c
            b = b xor c.ushr(2)
            e += b
            c += d
            c = c xor (d shl 8)
            f += c
            d += e
            d = d xor e.ushr(16)
            g += d
            e += f
            e = e xor (f shl 10)
            h += e
            f += g
            f = f xor g.ushr(4)
            a += f
            g += h
            g = g xor (h shl 8)
            b += g
            h += a
            h = h xor a.ushr(9)
            c += h
            a += b
            mem?.set(i, a)
            mem?.set(i + 1, b)
            mem?.set(i + 2, c)
            mem?.set(i + 3, d)
            mem?.set(i + 4, e)
            mem?.set(i + 5, f)
            mem?.set(i + 6, g)
            mem?.set(i + 7, h)
            i += 8
        }

        if (flag) {           /* second pass makes all of seed affect all of mem */
            i = 0
            while (i < SIZE) {
                a += mem!![i]
                b += mem!![i + 1]
                c += mem!![i + 2]
                d += mem!![i + 3]
                e += mem!![i + 4]
                f += mem!![i + 5]
                g += mem!![i + 6]
                h += mem!![i + 7]
                a = a xor (b shl 11)
                d += a
                b += c
                b = b xor c.ushr(2)
                e += b
                c += d
                c = c xor (d shl 8)
                f += c
                d += e
                d = d xor e.ushr(16)
                g += d
                e += f
                e = e xor (f shl 10)
                h += e
                f += g
                f = f xor g.ushr(4)
                a += f
                g += h
                g = g xor (h shl 8)
                b += g
                h += a
                h = h xor a.ushr(9)
                c += h
                a += b
                mem!![i] = a
                mem!![i + 1] = b
                mem!![i + 2] = c
                mem!![i + 3] = d
                mem!![i + 4] = e
                mem!![i + 5] = f
                mem!![i + 6] = g
                mem!![i + 7] = h
                i += 8
            }
        }

        Isaac()
        count = SIZE
    }

    companion object {
        const val SIZEL = 8              /* log of size of rsl[] and mem[] */
        const val SIZE = 1 shl SIZEL               /* size of rsl[] and mem[] */
        const val MASK = SIZE - 1 shl 2            /* for pseudorandom lookup */
    }
}