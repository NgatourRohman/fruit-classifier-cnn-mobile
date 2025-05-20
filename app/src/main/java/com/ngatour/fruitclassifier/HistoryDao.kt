package com.ngatour.fruitclassifier

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: ClassificationHistoryEntity)

    @Query("SELECT * FROM classification_history ORDER BY id DESC")
    suspend fun getAll(): List<ClassificationHistoryEntity>

    @Query("DELETE FROM classification_history")
    suspend fun deleteAll()

    @Query("DELETE FROM classification_history WHERE id = :id")
    suspend fun deleteById(id: Int)

}
