package com.example.words.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY id ASC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordEntity)

    @Update
    suspend fun update(word: WordEntity)

    @Delete
    suspend fun delete(word: WordEntity)

    // дараагийн / өмнөх үг зэрэгт хэрэгтэй байж болох индексээр авах
    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): WordEntity?
}
