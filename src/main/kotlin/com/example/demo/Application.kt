package com.example.demo

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory


class Server(private val port: Int) {
    val logger = LoggerFactory.getLogger(javaClass)

    val server = ServerBuilder
        .forPort(port)
        .addService(UserService())
        .addService(ProtoReflectionService.newInstance())
        .build()

    fun start() {
        server.start()
        logger.info("Server started, listening on $port")

        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.info("*** shutting down gRPC server since JVM is shutting down")
                this@Server.shutdown()
                logger.info("*** server shut down")
            }
        )
    }

    fun shutdown() = server.shutdown()
    fun awaitTermination() = server.awaitTermination()
}

fun main() {
    Database.connect(
        Config.jdbcDatabaseUrl,
        driver = "org.postgresql.Driver"
    )

    val server = Server(Config.port)
    server.start()
    server.awaitTermination()
}
