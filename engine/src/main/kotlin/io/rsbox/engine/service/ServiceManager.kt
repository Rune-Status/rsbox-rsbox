package io.rsbox.engine.service
import mu.KLogging
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Kyle Escobar
 */

object ServiceManager : KLogging() {

    private val services = ConcurrentHashMap<Service, Boolean>()

    fun init() {
        /**
         * Load all service instances
         */
    }

    private fun <T : Service> load(serviceClass: Class<T>) {
        val service: T
        try {

            val con = serviceClass.getConstructor()
            service = con.newInstance()
            service.start()

            services[service] = true

            logger.info("Started service {}.", serviceClass.simpleName)
        } catch(e : Exception) {
            logger.error("Failed to load service {}.", serviceClass.simpleName)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Service> find(service: Class<T>): T? {
        services.forEach { s ->
            if(s::class.java == service) {
                return s as T
            }
        }
        return null
    }

    fun isRunning(service: Class<out Service>): Boolean {
        val s = find(service)
        return if(s != null) {
            services[s]!!
        } else {
            false
        }
    }

    fun start(service: Class<out Service>): Boolean {
        val s = find(service) ?: return false
        if(services[s]!!) return false
        s.start()
        services[s] = true
        return true
    }

    fun stop(service: Class<out Service>): Boolean {
        val s = find(service) ?: return false
        if(!services[s]!!) return false
        s.stop()
        services[s] = false
        return true
    }

    operator fun <T : Service> get(service: Class<T>): T? = find(service)
}