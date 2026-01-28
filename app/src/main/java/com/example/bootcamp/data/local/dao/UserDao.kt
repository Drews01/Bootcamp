package com.example.bootcamp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bootcamp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/** Data Access Object for User entity. Provides methods for CRUD operations on the users table. */
@Dao
interface UserDao {

    /** Insert a user into the database. If conflict, replace the existing user. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /** Update an existing user. */
    @Update suspend fun updateUser(user: UserEntity)

    /** Delete a user from the database. */
    @Delete suspend fun deleteUser(user: UserEntity)

    /** Get a user by ID. */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    /** Get a user by username or email. */
    @Query(
        "SELECT * FROM users WHERE username = :usernameOrEmail OR email = :usernameOrEmail LIMIT 1"
    )
    suspend fun getUserByUsernameOrEmail(usernameOrEmail: String): UserEntity?

    /** Get the currently logged-in user (user with a token). */
    @Query("SELECT * FROM users WHERE token IS NOT NULL LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    /** Get all cached users. */
    @Query("SELECT * FROM users ORDER BY lastUpdated DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    /** Clear all users from the database. */
    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    /** Clear the token for all users (logout). */
    @Query("UPDATE users SET token = NULL")
    suspend fun clearAllTokens()
}
