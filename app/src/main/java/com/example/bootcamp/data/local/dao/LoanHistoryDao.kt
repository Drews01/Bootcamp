package com.example.bootcamp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bootcamp.data.local.entity.LoanHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for loan history caching operations.
 * Caches loan applications for offline display.
 */
@Dao
interface LoanHistoryDao {

    /** Insert or update multiple loan history items. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(loans: List<LoanHistoryEntity>)

    /** Get all cached loan history, ordered by date descending. */
    @Query("SELECT * FROM loan_history ORDER BY createdAt DESC")
    suspend fun getAllHistory(): List<LoanHistoryEntity>

    /** Observe all cached loan history as Flow. */
    @Query("SELECT * FROM loan_history ORDER BY createdAt DESC")
    fun observeHistory(): Flow<List<LoanHistoryEntity>>

    /** Get loan by ID. */
    @Query("SELECT * FROM loan_history WHERE loanApplicationId = :id")
    suspend fun getLoanById(id: Long): LoanHistoryEntity?

    /** Clear all cached history. */
    @Query("DELETE FROM loan_history")
    suspend fun clearAll()

    /** Get count of cached loans. */
    @Query("SELECT COUNT(*) FROM loan_history")
    suspend fun getCount(): Int
}
