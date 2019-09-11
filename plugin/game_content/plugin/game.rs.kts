import io.rsbox.api.event.impl.PlayerAuthEvent

on_event<PlayerAuthEvent> {
    val player = it.player
    if(player.username == "kyle") {
        it.cancel()
        it.loginStateResponse = LoginStateResponse.ACCOUNT_BANNED
    }
}