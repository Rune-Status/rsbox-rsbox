package io.rsbox.server

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.system.rsa.RSA
import io.rsbox.util.Hex
import mu.KLogging
import net.runelite.cache.fs.Store
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.security.SecureRandom
import java.util.zip.ZipFile

/**
 * @author Kyle Escobar
 */

object Install : KLogging() {

    val rsa = RSA()

    private val dirs = arrayOf(
        "rsbox/",
        "rsbox/config/",
        "rsbox/data/",
        "rsbox/data/cache/",
        "rsbox/data/xteas/",
        "rsbox/data/rsa/",
        "rsbox/data/saves/",
        "rsbox/logs/"
    )

    fun run(forceInstall: Boolean = false) {
        if(!checkSetup() || forceInstall) {

            println("\n" +
                    "  _____   _____ ____   ______   __\n" +
                    " |  __ \\ / ____|  _ \\ / __ \\ \\ / /\n" +
                    " | |__) | (___ | |_) | |  | \\ V / \n" +
                    " |  _  / \\___ \\|  _ <| |  | |> <  \n" +
                    " | | \\ \\ ____) | |_) | |__| / . \\ \n" +
                    " |_|  \\_\\_____/|____/ \\____/_/ \\_\\\n" +
                    "                                  \n" +
                    "                                  ")
            println("========== SETUP WIZARD ==========")

            this.setupDirs()

            this.setupConfigs()

            this.downloadCache()

            rsa.generate()

            this.generateEncryptionKey()

            println(" ")
            println("======= RSBOX SETUP COMPLETE =======")
            println("You may now start your server. Make sure you import the /rsbox/data/rsa/modulus.txt into your OSRS client.")
        } else {
            logger.info("This project has already been installed and is ready to start.")
            logger.info("If you want to force a re-install, use the argument --force on the [server:install] gradle task or add it after the --install argument via CLI.")
            System.exit(-1)
        }
    }

    fun checkInstalled(): Boolean {
        // Check if all dirs exits.
        dirs.forEach { dir ->
            val f = File(dir)
            if(!f.exists()) return false
        }

        // Check if configs have been loaded.
        if(!File(PathConstants.CONFIG_SERVER_PATH).exists()) {
            return false
        }

        return true
    }

    fun checkSetup(): Boolean {
        // Check if cache exists and can be loaded.
        try {
            val cache = Store(File(PathConstants.CACHE_PATH))
            cache.load()
            if(cache.indexes.size == 0) return false
        } catch(e : Exception) {
            return false
        }

        // Check if xteas.json is present.
        if(!File(PathConstants.XTEAS_FILE_PATH).exists()) {
            return false
        }

        return checkInstalled()
    }

    private fun setupDirs() {
        logger.info { "Scanning required directories." }

        dirs.forEach { dir ->
            val f = File(dir)
            if(!f.exists()) {
                f.mkdirs()
                logger.info("Created required directory {}.", f.path)
            }
        }
    }

    private fun setupConfigs() {
        Config { addSpec(ServerSpec) }.toYaml.toFile(PathConstants.CONFIG_SERVER_PATH)
        logger.info("Created default config file {}.", PathConstants.CONFIG_SERVER_PATH)
    }

    fun downloadCache() {
        println("Downloading Cache...")
        val channel = Channels.newChannel(URL(PathConstants.CACHE_REPO.replace("<>", Config{addSpec(ServerSpec)}[ServerSpec.revision].toString())).openStream())
        val output = FileOutputStream("${PathConstants.CACHE_PATH}cache.zip")
        val fileChannel = output.channel
        fileChannel.transferFrom(channel, 0, Long.MAX_VALUE)
        println("Download complete.")

        this.downloadXteas()
    }

    private fun downloadXteas() {
        println("Downloading Xteas...")
        val channel = Channels.newChannel(URL(PathConstants.XTEAS_REPO.replace("<>", Config{addSpec(ServerSpec)}[ServerSpec.revision].toString())).openStream())
        val output = FileOutputStream(PathConstants.XTEAS_FILE_PATH)
        output.channel.transferFrom(channel, 0, Long.MAX_VALUE)
        println("Download complete.")

        this.extractCache()
    }

    private fun extractCache() {
        println("Decompressing cache files...")
        val file = File("${PathConstants.CACHE_PATH}cache.zip")
        ZipFile(file).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    File("${PathConstants.CACHE_PATH}${entry.name}").outputStream().use { output ->
                        input.copyTo(output)
                        println("Decompressed file ${entry.name}")
                        output.close()
                    }
                    input.close()
                }
            }
            zip.close()
        }

        File("${PathConstants.CACHE_PATH}cache.zip").delete()

        println("======= DOWNLOAD COMPLETE =======")
    }

    private fun generateEncryptionKey() {
        val config = Config { addSpec(ServerSpec) }.from.yaml.file(PathConstants.CONFIG_SERVER_PATH)

        val key = ByteArray(16)
        SecureRandom.getInstanceStrong().nextBytes(key)

        val keyString = Hex.toHexString(key)

        config[ServerSpec.encryption_key] = keyString
        config.toYaml.toFile(PathConstants.CONFIG_SERVER_PATH)

        logger.info("Generated new random encryption key in server.yml.")
    }

}