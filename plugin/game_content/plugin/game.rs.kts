import io.rsbox.api.event.impl.PlayerLoadEvent

val game = Game()

on_event<PlayerLoadEvent> { game.initPlayerGameScreen(it.player) }