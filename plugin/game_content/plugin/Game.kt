import io.rsbox.api.entity.Player
import io.rsbox.api.interf.InterfaceType

/**
 * @author Kyle Escobar
 */

class Game {

    fun initPlayerGameScreen(player: Player) {
        player.interfaces.openGameScreen(player.interfaces.displayMode)

        InterfaceType.values.filter { it.interfaceId != -1 }.forEach {
            player.interfaces.openInterface(it.interfaceId, it)
        }
    }

}