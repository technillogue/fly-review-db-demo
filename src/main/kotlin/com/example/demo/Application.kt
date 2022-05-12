package com.example.demo

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Server(private val port: Int) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    val server: io.grpc.Server = ServerBuilder
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

    private fun shutdown(): io.grpc.Server = server.shutdown()
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
