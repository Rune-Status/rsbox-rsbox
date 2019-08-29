package io.rsbox.engine.system.rsa

import io.rsbox.config.PathConstants
import mu.KLogging
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import org.bouncycastle.util.io.pem.PemWriter
import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec

/**
 * @author Kyle Escobar
 */

class RSA {

    lateinit var exponent: BigInteger

    lateinit var modulus: BigInteger

    private lateinit var privateKey: RSAPrivateKey
    private lateinit var publicKey: RSAPublicKey

    private val radix = 16
    private val bits = 2048
    private val privateKeyPath = Paths.get(PathConstants.RSA_PRIVATE_PATH)
    private val publicKeyPath = Paths.get(PathConstants.RSA_PUBLIC_PATH)
    private val modulusPath = Paths.get(PathConstants.RSA_MODULUS_PATH)

    fun load() {
        val file = File(PathConstants.RSA_PRIVATE_PATH)
        if(!file.exists()) {
            logger.error("The RSA keys have not been generated. Please run the [server:install] gradle task or add the --install argument.")
            System.exit(-1)
        }

        try {
            PemReader(Files.newBufferedReader(file.toPath())).use { reader ->
                val pem = reader.readPemObject()
                val keySpec = PKCS8EncodedKeySpec(pem.content)

                Security.addProvider(BouncyCastleProvider())

                val factory = KeyFactory.getInstance("RSA", "BC")

                privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey
                exponent = privateKey.privateExponent
                modulus = privateKey.modulus

                logger.info("Loaded RSA key pairs.")
            }
        } catch(e : Exception) {
            logger.error("Unable to read RSA private key file.", e)
        }
    }

    fun generate() {
        Security.addProvider(BouncyCastleProvider())

        val kpg = KeyPairGenerator.getInstance("RSA", "BC")
        kpg.initialize(bits)
        val kp = kpg.generateKeyPair()

        privateKey = kp.private as RSAPrivateKey
        publicKey = kp.public as RSAPublicKey

        exponent = privateKey.privateExponent
        modulus = privateKey.modulus

        try {
            PemWriter(Files.newBufferedWriter(privateKeyPath)).use { writer ->
                writer.writeObject(PemObject("RSA PRIVATE KEY", privateKey.encoded))
            }

            PemWriter(Files.newBufferedWriter(publicKeyPath)).use { writer ->
                writer.writeObject(PemObject("RSA PUBLIC KEY", publicKey.encoded))
            }

            Files.newBufferedWriter(modulusPath).use { writer ->
                writer.write(modulus.toString(radix))
            }
        } catch(e : Exception) {
            logger.error("Error generating RSA key pair files.", e)
        }
    }

    companion object : KLogging()
}