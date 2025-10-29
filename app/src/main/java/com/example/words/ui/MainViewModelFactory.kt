package com.example.words.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.words.data.AppDatabase
import com.example.words.data.SettingsRepository
import com.example.words.data.WordRepository

class MainViewModelFactory(private val context: Context)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.Companion.getInstance(context)
        val wordRepo = WordRepository(db.wordDao())
        val settingsRepo = SettingsRepository(context)

        @Suppress("UNCHECKED_CAST")
        return MainViewModel(wordRepo, settingsRepo) as T
    }
}