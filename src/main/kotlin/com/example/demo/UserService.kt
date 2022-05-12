package com.example.demo

import demo.v1.Demo
import demo.v1.UserServiceGrpcKt
import demo.v1.getUserResponse
import demo.v1.setUserResponse
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class UserService : UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    private val logger = LoggerFactory.getLogger(javaClass)

    private fun exposedUserToProtobufUser(user: User?): Demo.User {
        logger.info("exposed user id ${user?.id?.value} name ${user?.name} about ${user?.about}")
        return demo.v1.user {
            if (user != null) {
                this.userId = user.id.value
                this.name = user.name.toString()
                this.about = user.about.toString()
            }
        }
    }
    override suspend fun getUser(request: Demo.GetUserRequest): Demo.GetUserResponse {
        logger.info(request.toString())
        val user = transaction {
            User.find { Users.name eq request.name }.firstOrNull()
        }
        val response = getUserResponse { this.user = exposedUserToProtobufUser(user) }
        logger.info(response.toString())
        return response
    }

    override suspend fun setUser(request: Demo.SetUserRequest): Demo.SetUserResponse {
        logger.info(request.toString())
        val user = transaction {
            val foundUser = User.findById(request.user.userId)
            val id = if (foundUser != null) {
                logger.info("found a user with id ${request.user.userId}")
                if (!request.user.name.isNullOrBlank()) foundUser.name = request.user.name
                if (!request.user.about.isNullOrBlank()) foundUser.about = request.user.about
                request.user.userId
            } else User.new {
                this.name = request.user.name
                this.about = request.user.about
            }.id.value
            // for some reason just User.new only contains id but not name or about
            User.findById(id)
        }
        val response = setUserResponse { this.user = exposedUserToProtobufUser(user) }
        logger.info(response.toString())
        return response
    }
}
