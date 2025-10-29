package com.example.words.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.words.data.SettingsRepository
import com.example.words.data.WordEntity
import com.example.words.data.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class UiState(
    val words: List<WordEntity> = emptyList(),
    val currentIndex: Int = 0,
    val mode: Int = 1,
    val revealed: Boolean = false // ← шинэ
)

class MainViewModel(
    private val repo: WordRepository,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repo.getAllWords(),
                settingsRepo.modeFlow
            ) { words, mode ->
                // mode өөрчлөгдөхөд revealed хэр байх ёстой вэ?
                val autoReveal = if (mode == 3) true else false

                _ui.value.copy(
                    words = words,
                    mode = mode,
                    revealed = autoReveal
                )
            }.collect { newState ->
                val fixedIndex = newState.currentIndex.coerceIn(
                    0,
                    (newState.words.size - 1).coerceAtLeast(0)
                )
                _ui.value = newState.copy(currentIndex = fixedIndex)
            }
        }
    }

    // -> асуулт/хариултын хосыг буцаана
    // first = асуулт (үндсэн харуулах тал)
    // second = хариулт (далдлах тал)
    fun getQuestionAnswerPair(): Pair<String, String> {
        val s = _ui.value
        if (s.words.isEmpty()) return "" to ""

        val w = s.words[s.currentIndex]

        return when (s.mode) {
            1 -> {
                // "Гадаад үгийг харуулаад Монгол утгыг асууна"
                // Асуулт = English, Хариулт = Mongolian
                w.english to w.mongolian
            }
            2 -> {
                // "Монгол үгийг харуулаад Гадаад утгыг асууна"
                // Асуулт = Mongolian, Хариулт = English
                w.mongolian to w.english
            }
            3 -> {
                // "Хоёуланг нь асууна" = random аль нь асуулт болохыг сонгоно
                if (Random.nextBoolean()) {
                    w.english to w.mongolian
                } else {
                    w.mongolian to w.english
                }
            }
            else -> w.english to w.mongolian
        }
    }

    fun revealAnswer() {
        _ui.update { it.copy(revealed = true) }
    }

    fun next() {
        val s = _ui.value
        if (s.words.isEmpty()) return
        val newIdx = (s.currentIndex + 1).coerceAtMost(s.words.lastIndex)
        _ui.update {
            it.copy(
                currentIndex = newIdx,
                revealed = if (it.mode == 3) true else false
            )
        }
    }

    fun prev() {
        val s = _ui.value
        if (s.words.isEmpty()) return
        val newIdx = (s.currentIndex - 1).coerceAtLeast(0)
        _ui.update {
            it.copy(
                currentIndex = newIdx,
                revealed = if (it.mode == 3) true else false
            )
        }
    }

    fun randomWord() {
        val s = _ui.value
        if (s.words.isEmpty()) return
        val newIdx = Random.nextInt(s.words.size)
        _ui.update {
            it.copy(
                currentIndex = newIdx,
                revealed = if (it.mode == 3) true else false
            )
        }
    }


    fun setMode(mode: Int) {
        viewModelScope.launch {
            settingsRepo.setMode(mode)
            // mode өөрчлөгдөхөд автоматаар combine { ... } хэсгээр шинэ state орж ирнэ
        }
    }

    fun addWord(eng: String, mon: String) {
        viewModelScope.launch {
            repo.addWord(eng, mon)
        }
    }

    fun updateWord(id: Int, eng: String, mon: String) {
        viewModelScope.launch {
            repo.updateWord(id, eng, mon)
        }
    }

    fun deleteCurrentWord() {
        val s = _ui.value
        if (s.words.isEmpty()) return
        val w = s.words[s.currentIndex]
        viewModelScope.launch {
            repo.deleteWord(w)
        }
    }
}
