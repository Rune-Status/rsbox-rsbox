package io.rsbox.engine.service.database

import com.uchuhimo.konf.ConfigSpec
import io.rsbox.config.Conf

object MongoEnvironment : ConfigSpec("mongo") {

    val protocol by optional("", "protocol")
    val host by optional("localhost", "host")
    val user by optional("none", "user")
    val password by optional("none", "password")
    val params by optional("", "params")
    val database by optional("default", "database")

    /**
     * Creates a MongoDB connection string from the environment settings.
     */
    fun connectionString() : String {
        return if(Conf.MONGODB[password] != "none" && Conf.MONGODB[user] != "none") {
            """"
                    ${Conf.MONGODB[protocol]}
                    ${Conf.MONGODB[user]}:
                    ${Conf.MONGODB[password]}@
                    ${Conf.MONGODB[host]}/?
                    ${Conf.MONGODB[params]}/
                """.trimIndent()
        } else {
            """
                    ${Conf.MONGODB[protocol]}
                    ${Conf.MONGODB[host]}/?
                    ${Conf.MONGODB[params]}/
                """.trimIndent()
        }
    }
}

