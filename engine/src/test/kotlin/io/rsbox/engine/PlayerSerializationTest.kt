package io.rsbox.engine

import com.uchuhimo.konf.Config
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.system.serializer.player.PlayerCreator
import io.rsbox.engine.system.serializer.player.PlayerLoader
import io.rsbox.util.Hex
import org.junit.jupiter.api.Test
import java.io.File

/**
 * @author Kyle Escobar
 */

class PlayerSerializationTest {

    @Test
    fun playerCreateTest() {

        val path = File(PathConstants.PLAYER_SAVES_PATH)
        if (!path.exists()) path.mkdirs()

        val key = "0123456789ABCDEF"
        val username = "test"
        val password = "test123"

        Conf.SERVER = Config { addSpec(ServerSpec) }
        Conf.SERVER[ServerSpec.encryption_key] = Hex.toHexString(key.toByteArray())

        PlayerCreator.createPlayer(username, password)

        val file = File("${PathConstants.PLAYER_SAVES_PATH}$username.yml")

        assert(file.exists())

        assert(PlayerLoader.checkCredentials(username, password))
    }
}

