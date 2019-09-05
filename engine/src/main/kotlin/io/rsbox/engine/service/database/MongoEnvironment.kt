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

    fun connectionString() : String {
        if(Conf.MONGODB.get(password) != "none" && Conf.MONGODB.get(user) != "none") {
            return "${Conf.MONGODB.get(protocol)}" +
                    "${Conf.MONGODB.get(user)}:" +
                    "${Conf.MONGODB.get(password)}@" +
                    "${Conf.MONGODB.get(host)}/?" +
                    "${Conf.MONGODB.get(params)}/"
        } else {
            return "${Conf.MONGODB.get(protocol)}" +
                    "${Conf.MONGODB.get(host)}/?" +
                    "${Conf.MONGODB.get(params)}/"
        }
    }
}