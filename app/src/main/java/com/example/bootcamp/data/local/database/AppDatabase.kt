package com.example.bootcamp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bootcamp.data.local.dao.BranchDao
import com.example.bootcamp.data.local.dao.LoanHistoryDao
import com.example.bootcamp.data.local.dao.PendingLoanDao
import com.example.bootcamp.data.local.dao.PendingProfileDao
import com.example.bootcamp.data.local.dao.UserDao
import com.example.bootcamp.data.local.dao.UserProfileCacheDao
import com.example.bootcamp.data.local.dao.UserTierDao
import com.example.bootcamp.data.local.entity.BranchEntity
import com.example.bootcamp.data.local.entity.LoanHistoryEntity
import com.example.bootcamp.data.local.entity.PendingLoanEntity
import com.example.bootcamp.data.local.entity.PendingProfileEntity
import com.example.bootcamp.data.local.entity.UserEntity
import com.example.bootcamp.data.local.entity.UserProfileCacheEntity
import com.example.bootcamp.data.local.entity.UserTierEntity

/** Room database for the Bootcamp application. Contains all DAOs and entity definitions. */
@Database(
    entities = [
        UserEntity::class,
        PendingLoanEntity::class,
        PendingProfileEntity::class,
        BranchEntity::class,
        UserTierEntity::class,
        LoanHistoryEntity::class,
        UserProfileCacheEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** Get the UserDao for user-related operations. */
    abstract fun userDao(): UserDao

    /** Get the PendingLoanDao for pending loan operations. */
    abstract fun pendingLoanDao(): PendingLoanDao

    /** Get the PendingProfileDao for pending profile operations. */
    abstract fun pendingProfileDao(): PendingProfileDao

    /** Get the BranchDao for cached branch operations. */
    abstract fun branchDao(): BranchDao

    /** Get the UserTierDao for cached tier operations. */
    abstract fun userTierDao(): UserTierDao

    /** Get the LoanHistoryDao for cached loan history operations. */
    abstract fun loanHistoryDao(): LoanHistoryDao

    /** Get the UserProfileCacheDao for cached profile operations. */
    abstract fun userProfileCacheDao(): UserProfileCacheDao

    companion object {
        const val DATABASE_NAME = "bootcamp_database"
    }
}


