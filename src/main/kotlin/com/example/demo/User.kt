package com.example.demo

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable(name = "users") {
    val name = text("name", eagerLoading = true).nullable()
    var about = text("about", eagerLoading = true).nullable()
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<User>(Users)
    var name by Users.name
    var about by Users.about
}

