package com.example.bootcamp.data.mapper

import com.example.bootcamp.data.local.entity.UserEntity
import com.example.bootcamp.domain.model.User

/**
 * Mapper functions for converting between Entity and Domain models. Follows Clean Architecture by
 * keeping layers independent.
 */
object UserMapper {

    /** Convert UserEntity (data layer) to User (domain layer). */
    fun UserEntity.toDomain(): User {
        return User(
                id = this.id,
                username = this.username,
                email = this.email,
                token = this.token,
                isLoggedIn = this.token != null
        )
    }

    /** Convert User (domain layer) to UserEntity (data layer). */
    fun User.toEntity(): UserEntity {
        return UserEntity(
                id = this.id,
                username = this.username,
                email = this.email,
                token = this.token,
                lastUpdated = System.currentTimeMillis()
        )
    }

    /** Convert list of UserEntity to list of User. */
    fun List<UserEntity>.toDomainList(): List<User> {
        return this.map { it.toDomain() }
    }
}
