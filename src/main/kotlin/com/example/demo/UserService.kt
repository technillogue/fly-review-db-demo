package com.example.demo

import org.jetbrains.exposed.sql.transactions.transaction
import demo.v1.UserServiceGrpcKt
import demo.v1.Demo
import demo.v1.getUserResponse
import demo.v1.setUserResponse
import org.slf4j.LoggerFactory

class UserService : UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    val logger = LoggerFactory.getLogger(javaClass)

    private fun exposedUserToProtobufUser(user: User?): Demo.User {
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
        val response = getUserResponse {this.user = exposedUserToProtobufUser(user)}
        logger.info(response.toString())
        return response
    }

    override suspend fun setUser(request: Demo.SetUserRequest): Demo.SetUserResponse {
        logger.info(request.user.toString())
        val user = transaction {
            val foundUser = User.findById(request.user.userId)
            logger.info(foundUser?.name)
            logger.info(foundUser?.about)
            if (foundUser != null) {
                if (request.user.name != null) foundUser.name = request.user.name
                if (request.user.about != null) foundUser.about = request.user.about
                foundUser
            } else User.new{
                this.name = name
                this.about = about
            }
        }
        val response = setUserResponse {this.user = exposedUserToProtobufUser(user)}
        logger.info(response.toString())
        return response
    }
}