package io.rsbox.engine.service
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.game.model.World
import io.rsbox.engine.service.game.GameService
import io.rsbox.engine.service.login.LoginService
import io.rsbox.engine.service.xtea.XteaKeyService
import mu.KLogging

/**
 * @author Kyle Escobar
 */

object ServiceManager : KLogging() {

    private val services = hashMapOf<Class<out Service>, Service>()

    /**
     * Load all service instances
     */
    fun init(world: World) {
        load(GameService::class.java, GameService(world))
        load(LoginService::class.java, LoginService())
        load(XteaKeyService::class.java, XteaKeyService(world.engine))
    }

    private fun <T : Service> load(serviceClass: Class<out T>, service: T) {
        if(services.containsKey(serviceClass)) {
            logger.error("Unable to load service {} as it has already been started.", serviceClass.simpleName)
            return
        }

        service.start()
        services[serviceClass] = service
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Service> find(service: Class<out T>): T? {
        services.forEach { s ->
            if(s.key == service) {
                return s.value as T
            }
        }
        return null
    }

    fun isRunning(service: Class<out Service>): Boolean {
        return services.containsKey(service)
    }

    fun stop(service: Class<out Service>): Boolean {
        val s = find(service) ?: return false
        s.stop()
        services.remove(service)
        return true
    }

    operator fun <T : Service> get(service: Class<out T>): T? = find(service)
}