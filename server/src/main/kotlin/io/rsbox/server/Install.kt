package io.rsbox.server

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import mu.KLogging
import net.runelite.cache.fs.Store
import java.io.File

/**
 * @author Kyle Escobar
 */

object Install : KLogging() {

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
        if(!checkInstalled() && !forceInstall) {

            logger.info { "Installing RSBox project..." }

            this.setupDirs()

            this.setupConfigs()

            logger.info { "Installation complete! Make sure that you upload your cache to rsbox/data/cache/ and xteas.json to rsbox/data/xteas/." }
            logger.info { "In order to connect, first you need to add the contents of rsbox/data/rsa/modulus.txt into your client. Refer to the Wiki for more instructions." }

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

}