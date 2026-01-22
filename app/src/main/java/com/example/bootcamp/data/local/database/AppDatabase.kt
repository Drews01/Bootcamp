package com.example.bootcamp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bootcamp.data.local.dao.UserDao
import com.example.bootcamp.data.local.entity.UserEntity

/** Room database for the Bootcamp application. Contains all DAOs and entity definitions. */
@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /** Get the UserDao for user-related operations. */
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "bootcamp_database"
    }
}
