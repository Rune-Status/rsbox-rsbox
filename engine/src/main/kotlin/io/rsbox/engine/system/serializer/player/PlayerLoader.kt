package io.rsbox.engine.system.serializer.player

import com.uchuhimo.konf.Config
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.PlayerSpec
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.game.model.entity.Player
import io.rsbox.net.session.Session
import io.rsbox.util.Hex
import io.rsbox.util.boxhash.BoxHasher
import java.io.File

/**
 * @author Kyle Escobar
 */

object PlayerLoader {

    fun checkCredentials(username: String, password: String): Boolean {

        val salt = Hex.fromHexString(Conf.SERVER[ServerSpec.encryption_key])
        val hash = BoxHasher.toHexString(BoxHasher.hash(salt, password.toByteArray()))

        val file = File("${PathConstants.PLAYER_SAVES_PATH}$username.yml")
        if(!file.exists()) return false

        val save = Config { addSpec(PlayerSpec) }.from.yaml.file(file)

        if(save[PlayerSpec.username] == username && save[PlayerSpec.password] == hash) {
            return true
        }

        return false
    }

    fun loadPlayer(username: String, session: Session): Player? {
        val file = File("${PathConstants.PLAYER_SAVES_PATH}$username.yml")

        if(!file.exists()) return null

        val save = Config { addSpec(PlayerSpec) }.from.yaml.file(file)

        if(save[PlayerSpec.username] != username) return null

        val player = Player()

        player.session = session

        player.username = save[PlayerSpec.username]
        player.passwordHash = save[PlayerSpec.password]
        player.displayName = save[PlayerSpec.display_name]
        player.privilege = save[PlayerSpec.privilege]
        player.uuid = save[PlayerSpec.uuid]
        player.banned = save[PlayerSpec.banned]

        return player
    }

}