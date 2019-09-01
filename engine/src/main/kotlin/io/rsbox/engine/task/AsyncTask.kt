package io.rsbox.engine.task

import mu.KLogging
import java.util.concurrent.Phaser

/**
 * @author Kyle Escobar
 */

object AsyncTask : KLogging() {

    fun run(phaser: Phaser, logic: () -> Unit) {
        try {
            logic()
        } catch(e : Exception) {
            logger.error("Error occurred when executing async task.", e)
        } finally {
            phaser.arriveAndDeregister()
        }
    }

}