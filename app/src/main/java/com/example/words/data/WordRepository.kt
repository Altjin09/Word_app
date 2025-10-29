package com.example.words.data

import kotlinx.coroutines.flow.Flow

class WordRepository(
    private val wordDao: WordDao
) {

    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()

    suspend fun addWord(eng: String, mon: String) {
        wordDao.insert(WordEntity(english = eng, mongolian = mon))
    }

    suspend fun updateWord(id: Int, eng: String, mon: String) {
        wordDao.update(WordEntity(id = id, english = eng, mongolian = mon))
    }

    suspend fun deleteWord(word: WordEntity) {
        wordDao.delete(word)
    }
}
