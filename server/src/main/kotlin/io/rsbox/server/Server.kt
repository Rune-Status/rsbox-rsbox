package io.rsbox.server

import io.rsbox.engine.Engine
import mu.KLogging


/**
 * @author Kyle Escobar
 */

class Server {

    private lateinit var engine: Engine

    fun start() {
        logger.info { "Server is starting up..." }

        // Check if installed
        if(!Install.checkSetup()) {
            logger.error { "RSBox has not been installed yet. Please run the [server:install] gradle task or add --install CLI flag before starting the server." }
            System.exit(-1)
        }

        logger.info { "Server installation check passed." }

        this.loadEngine()

        logger.info { "Server startup has completed." }
    }

    private fun loadEngine() {
        logger.info { "Starting game engine." }
        engine = Engine()
        engine.start()
    }

    companion object : KLogging()
}