package io.rsbox.engine.service.database

import com.mongodb.async.client.MongoClient
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.engine.service.Service
import mu.KLogging
import org.litote.kmongo.async.KMongo

class MongoDBService() : Service, KLogging() {

    private val client : MongoClient by lazy { KMongo.createClient(MongoEnvironment.connectionString()) }

    private val database by lazy { client.getDatabase(Conf.MONGODB[MongoEnvironment.database]) }

    override fun start() {
        logger.info { "Starting MongoDB service..." }
        Conf.MONGODB = Config { addSpec(MongoEnvironment) }.from.yaml.file(PathConstants.CONFIG_MONGO_PATH)
        Conf.MONGODB.toYaml.toFile(PathConstants.CONFIG_MONGO_PATH)

        if(database != null) {
            logger.info { "MongoDB connected successfully." }
        }
    }

    override fun stop() {
        logger.info { "Stopping MongoDB service..." }
        client.close()
        logger.info { "MongoDB connection closed." }
    }

    fun getCollection(collection : String) = database.getCollection(collection)

}