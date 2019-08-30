package io.rsbox.engine.login

import mu.KLogging
import java.net.SocketAddress
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author Kyle Escobar
 */

object LoginQueue : KLogging() {

    val queue = LinkedBlockingQueue<GameLoginRequest>()

    fun addLoginRequeust(request: GameLoginRequest, srcIp: SocketAddress) {
        logger.info("Login request queued for username={} from ip={}.", request.username, srcIp)
        queue.offer(request)
    }
}