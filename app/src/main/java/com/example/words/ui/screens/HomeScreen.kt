package com.example.words.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.words.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: MainViewModel,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onSettings: () -> Unit
) {
    val state by vm.ui.collectAsState()
    val currentWord = state.words.getOrNull(state.currentIndex)

    // устгахдаа асуух диалогын state
    var showDeleteDialog by remember { mutableStateOf(false) }

    // асуулт + хариулт ямар үг байх вэ (mode-оос хамаарч)
    val (questionText, answerText) = remember(state.currentIndex, state.mode, currentWord?.id) {
        if (currentWord == null) {
            "" to ""
        } else {
            when (state.mode) {
                1 -> currentWord.english to currentWord.mongolian
                2 -> currentWord.mongolian to currentWord.english
                3 -> {
                    if (kotlin.random.Random.nextBoolean()) {
                        currentWord.english to currentWord.mongolian
                    } else {
                        currentWord.mongolian to currentWord.english
                    }
                }
                else -> currentWord.english to currentWord.mongolian
            }
        }
    }

    val quizModeShowBoth = state.mode == 3
    val hasWord = currentWord != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("үг цээжлэх апп", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { onSettings() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Тохиргоо",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ҮГНИЙ ХЭСЭГ
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Асуулт хайрцаг
                WordBox(
                    text = questionText.ifBlank { "..." },
                    bold = true,
                    enabledLongPressToEdit = hasWord,
                    onLongPress = { onEdit() }
                )

                Spacer(Modifier.height(16.dp))

                // Хариулт хайрцаг
                val shownAnswer = when {
                    quizModeShowBoth -> answerText.ifBlank { "..." }
                    state.revealed   -> answerText.ifBlank { "..." }
                    else             -> "үг харах"
                }

                AnswerBox(
                    text = shownAnswer,
                    revealEnabled = !quizModeShowBoth && !state.revealed && hasWord,
                    onReveal = { vm.revealAnswer() },
                    onLongPress = { onEdit() }
                )
            }

            // Доод товчнууд
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 7-р шаардлага:
                // үг байхгүй бол зөвхөн "НЭМЭХ" идэвхтэй бусад нь disabled
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PurpleButton("НЭМЭХ", enabled = true) { onAdd() }
                    PurpleButton("ЗАСВАРЛАХ", enabled = hasWord) { onEdit() }
                    PurpleButton("УСТГА", enabled = hasWord) { showDeleteDialog = true }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PurpleButton("ДАРАА", enabled = hasWord) { vm.next() }
                    PurpleButton("ӨМНӨХ", enabled = hasWord) { vm.prev() }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PurpleButton("RANDOM", enabled = hasWord) { vm.randomWord() }
                }
            }
        }

        // 9-р шаардлага: устгахдаа баталгаажуулах диалог
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Устгах уу?", color = Color.White) },
                text = {
                    Text(
                        "Энэ үгийг үнэхээр устгах уу?",
                        color = Color.White
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.deleteCurrentWord()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("ТИЙМ", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("ҮГҮЙ", color = Color.White)
                    }
                },
                containerColor = Color(0xFF222222)
            )
        }
    }

}

@Composable
fun WordBox(
    text: String,
    bold: Boolean,
    enabledLongPressToEdit: Boolean,
    onLongPress: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF444444))
            .combinedClickable(
                onClick = {},
                onLongClick = { if (enabledLongPressToEdit) onLongPress() }
            )
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AnswerBox(
    text: String,
    revealEnabled: Boolean,
    onReveal: () -> Unit,
    onLongPress: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF444444))
            .combinedClickable(
                onClick = { if (revealEnabled) onReveal() },
                onLongClick = { onLongPress() }
            )
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 28.sp
        )
    }
}

@Composable
fun PurpleButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF4A148C),
            disabledContentColor = Color.LightGray
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Text(text)
    }
}
