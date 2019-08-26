package io.rsbox.server

/**
 * This object is used to launch and handle arguments for RSServer
 *
 * @author Kyle Escobar
 */
object Launcher {

    /**
     * RSServer static reference. Can be called from any class.
     */
    lateinit var server: Server

    @JvmStatic
    fun main(args: Array<String>) {
        server = Server()
        server.init()
    }

}