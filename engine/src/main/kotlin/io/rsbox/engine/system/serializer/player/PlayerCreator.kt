package io.rsbox.engine.system.serializer.player

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.PlayerSpec
import mu.KLogging
import net.openhft.hashing.LongHashFunction
import java.util.*

/**
 * @author Kyle Escobar
 */

object PlayerCreator : KLogging() {

    fun createPlayer(username: String, password: String) {
        val uuid = UUID.randomUUID()!!

        val save = Config { addSpec(PlayerSpec) }

        save[PlayerSpec.username] = username
        save[PlayerSpec.password] = LongHashFunction.xx().hashChars(password).toString()
        save[PlayerSpec.uuid] = uuid.toString()

        save.toYaml.toFile("${PathConstants.PLAYER_SAVES_PATH}$username.yml")

        logger.info("Created new player save for username {}.", username)
    }

}