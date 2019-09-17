package io.rsbox.server

/**
 * @author Kyle Escobar
 */

object Launcher {

    lateinit var server: Server
    
    @JvmStatic
    fun main(args: Array<String>) {
        if(args.isNotEmpty()) {
            if(args.size == 1 && args[0] == "--install") {
                Install.run()
            }
            else if(args.size == 2 && args[0] == "--install" && args[1] == "--force") {
                Install.run(true)
            }
        } else {
            server = Server()
            server.start()
        }
    }

}