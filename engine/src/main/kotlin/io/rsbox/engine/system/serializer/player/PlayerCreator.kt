package io.rsbox.engine.system.serializer.player

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.PlayerSpec
import io.rsbox.config.specs.ServerSpec
import io.rsbox.util.Hex
import io.rsbox.util.boxhash.BoxHasher
import mu.KLogging
import java.util.*

/**
 * @author Kyle Escobar
 */

object PlayerCreator : KLogging() {

    fun createPlayer(username: String, password: String) {
        val uuid = UUID.randomUUID()!!

        val save = Config { addSpec(PlayerSpec) }

        val salt = Hex.fromHexString(Conf.SERVER[ServerSpec.encryption_key])
        val passwordHash = BoxHasher.hash(salt, password.toByteArray())

        save[PlayerSpec.username] = username
        save[PlayerSpec.password] = BoxHasher.toHexString(passwordHash)
        save[PlayerSpec.uuid] = uuid.toString()

        save.toYaml.toFile("${PathConstants.PLAYER_SAVES_PATH}$username.yml")

        logger.info("Created new player save for username {}.", username)
    }

}