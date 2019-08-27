package io.rsbox.server.system.security.rsa

import io.rsbox.server.ServerConstants
import mu.KLogging
import java.io.File
import java.math.BigInteger
import java.nio.file.Paths
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

/**
 * @author Kyle Escobar
 */

/**
 * A static object class which handles generating, loading, and saving of
 * the RSA public / private key pairs.
 */
object RSA : KLogging() {

    /**
     * The private key exponent.
     */
    lateinit var exponent: BigInteger

    /**
     * The private key modulus.
     */
    lateinit var modulus: BigInteger

    private lateinit var publicKey: RSAPublicKey

    private lateinit var privateKey: RSAPrivateKey

    private const val radix = 16
    private const val bits = 2048
    private var privateKeyPath = Paths.get("../${ServerConstants.RSA_PRIVATE_FILE}")
    private var publicKeyPath = Paths.get("../${ServerConstants.RSA_PUBLIC_FILE}")
    private var modulusPath = Paths.get("../${ServerConstants.RSA_MODULUS_FILE}")

    /**
     * Loads the modulus and exponent from private key file.
     */
    fun load() {
        val file = File(ServerConstants.RSA_PRIVATE_FILE)
        if(!file.exists()) {
            val scanner = Scanner(System.`in`)
            println("No RSA keys where found. Generating a new one...")
        }

        try {

        } catch(e : Exception) {
            logger.error("Unable to read RSA private key file.", e)
        }
    }


}