package com.example.demo

import org.jetbrains.exposed.sql.transactions.transaction
import demo.v1.UserServiceGrpcKt
import demo.v1.Demo
import demo.v1.getUserResponse
import demo.v1.setUserResponse

class UserService : UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    private fun exposedUserToUserProto(user: User?): Demo.User {
        return demo.v1.user {
            if (user != null) {
                this.userId = user.id.value
                this.name = user.name.toString()
                this.about = user.about.toString()
            }
        }
    }
    override suspend fun getUser(request: Demo.GetUserRequest): Demo.GetUserResponse {
        val user = transaction {
            User.find {
                Users.name eq request.name
            }.firstOrNull()
        }
        return getUserResponse {this.user = exposedUserToUserProto(user)}
    }

    override suspend fun setUser(request: Demo.SetUserRequest): Demo.SetUserResponse {
        val name = request.user.name
        val about = request.user.about
        val id = request.user.userId
        val user = transaction {
            val foundUser = User.findById(id)
            if (foundUser != null) {
                foundUser.name = name ?: foundUser.name
                foundUser.about = about ?: foundUser.about
                foundUser
            } else {
                User.new{
                    this.name = name
                    this.about = about
                }
            }
        }
        return setUserResponse {this.user = exposedUserToUserProto(user)}
    }
}