package io.rsbox.server

import com.google.common.base.Stopwatch
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.server.config.Conf
import io.rsbox.server.config.spec.ServerSpec
import io.rsbox.server.net.GameServer
import io.rsbox.server.system.security.rsa.RSA
import mu.KLogging
import net.runelite.cache.fs.Store
import java.io.File
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

/**
 * This is the object which is the base of starting the server. It is called from the launcher on execution.
 */
class Server {

    lateinit var gameServer: GameServer

    private lateinit var stopwatch: Stopwatch

    /**
     * Initializes anything needed before starting the server.
     */
    fun init() {
        stopwatch = Stopwatch.createStarted()

        logger.info { "Initializing Server..." }

        this.setupDirs()

        this.setupConfigs()

        this.loadCache()

        this.run()
    }

    /**
     * Runs everything in order to start the server.
     */
    fun run() {
        this.start()
        this.bind()
        stopwatch.stop()

        logger.info("${Conf.SERVER[ServerSpec.name]} startup took {}s.", stopwatch.elapsed(TimeUnit.SECONDS))
    }

    /**
     * Starts the game engine.
     */
    fun start() {
        logger.info { "Starting server..." }

        /**
         * Load the server's revision from server.yml.
         */
        REVISION = Conf.SERVER[ServerSpec.revision]

        /**
         * Load / generate the RSA key pairs.
         */
        RSA.load()
    }

    /**
     * Signals a server shutdown.
     */
    fun shutdown() {

    }

    /**
     * Started the networking required for the server.
     */
    fun bind() {
        logger.info("Starting server network.")

        val address = Conf.SERVER[ServerSpec.network_address]
        val port = Conf.SERVER[ServerSpec.network_port]

        gameServer = GameServer(this)
        gameServer.bind(InetSocketAddress(address, port))
    }

    private fun setupDirs() {
        logger.info { "Scanning server directories." }

        val dirs = arrayOf(
            "rsbox/",
            "rsbox/config/",
            "rsbox/data/",
            "rsbox/plugins/",
            "rsbox/data/cache/",
            "rsbox/data/xteas/",
            "rsbox/data/rsa/",
            "rsbox/data/def/",
            "rsbox/data/save/",
            "rsbox/logs/"
        )

        dirs.forEach { dir ->
            val file = File(dir)
            if(!file.exists()) {
                file.mkdirs()
                logger.info("Created default directory {}.", dir)
            }
        }
    }

    private fun setupConfigs() {
        Conf.SERVER = loadConfig(File("rsbox/config/server.yml"), ServerSpec)
    }

    private fun loadConfig(file: File, spec: ConfigSpec): Config {
        if(!file.exists()) {
            Config { addSpec(spec) }.toYaml.toFile(file)
            logger.info("Created default configuration file {}.", file.path)
        }
        val config = Config { addSpec(spec) }.from.yaml.file(file)
        config.toYaml.toFile(file)

        logger.info("Loaded configuration file {}.", file.path)
        return config
    }

    private fun loadCache() {
        val stopwatch = Stopwatch.createStarted()
        /**
         * Check to make sure xteas.json exists before loading cache.
         */
        val xteasFile = File("rsbox/data/xteas/xteas.json")
        if(!xteasFile.exists()) {
            logger.error { "Unable to locate rsbox/data/xteas/xteas.json file. Make sure this file is present." }
            System.exit(-1)
        }

        val cacheFolder = File("rsbox/data/cache/")
        cacheStore = Store(cacheFolder)
        cacheStore.load()

        /**
         * Check to make sure there is actually a loaded cache store.
         * The check below simply sees if there are any loaded indices.
         */
        if(cacheStore.indexes.size == 0) {
            logger.error("Unable to load cache from rsbox/data/cache/. Make sure your desired cache is present.")
            System.exit(-1)
        }
        stopwatch.stop()

        logger.info("Loaded game cache store in {}ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS))
    }

    companion object : KLogging() {
        var REVISION: Int = -1

        lateinit var cacheStore: Store
    }
}