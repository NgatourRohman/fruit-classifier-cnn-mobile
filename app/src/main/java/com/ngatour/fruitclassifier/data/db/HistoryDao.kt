package com.ngatour.fruitclassifier.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: ClassificationHistoryEntity)

    @Query("SELECT * FROM classification_history ORDER BY id DESC")
    suspend fun getAll(): List<ClassificationHistoryEntity>

    @Query("DELETE FROM classification_history")
    suspend fun deleteAll()

    @Query("DELETE FROM classification_history WHERE id = :id")
    suspend fun deleteById(id: Int)

}