package com.creditpro.app.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.creditpro.app.models.CreditScore
import com.creditpro.app.models.User

// ── DAOs ──────────────────────────────────────────────────────────────────────

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): LiveData<User?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUserOnce(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}

@Dao
interface CreditScoreDao {
    @Query("SELECT * FROM credit_scores ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestScore(): LiveData<CreditScore?>

    @Query("SELECT * FROM credit_scores ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestScoreOnce(): CreditScore?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: CreditScore)

    @Query("DELETE FROM credit_scores")
    suspend fun deleteAllScores()

    @Query("SELECT * FROM credit_scores ORDER BY updatedAt ASC")
    suspend fun getAllScores(): List<CreditScore>
}

// ── Room Database ─────────────────────────────────────────────────────────────

@Database(
    entities = [User::class, CreditScore::class],
    version = 1,
    exportSchema = false
)
abstract class CreditProDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun creditScoreDao(): CreditScoreDao
}
