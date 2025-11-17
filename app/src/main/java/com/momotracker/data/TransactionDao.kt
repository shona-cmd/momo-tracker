package com.momotracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Received'")
    fun totalReceived(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Sent'")
    fun totalSent(): Flow<Double?>
}
