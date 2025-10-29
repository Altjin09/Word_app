package com.example.words.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.words.ui.MainViewModel

@Composable
fun EditScreen(
    vm: MainViewModel,
    wordId: Int,          // -1 бол шинэ
    initialEng: String,   // Navigation-аас ирсэн урьдчилсан English
    initialMon: String,   // Navigation-аас ирсэн урьдчилсан Mongolian
    onDone: () -> Unit,
) {
    // remember {} ашиглаж байна → анх ороход нэг л удаа инициализ хийж UI дээр харуулна
    var eng by remember(wordId) { mutableStateOf(initialEng) }
    var mon by remember(wordId) { mutableStateOf(initialMon) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = eng,
            onValueChange = { eng = it },
            label = { Text("Гадаад үг", color = Color.White) },
            placeholder = { Text("жишээ: green", color = Color.LightGray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF6200EE),
                focusedBorderColor = Color(0xFF6200EE),
                cursorColor = Color.Black,
                unfocusedLabelColor = Color.White,
                focusedLabelColor = Color.White,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = mon,
            onValueChange = { mon = it },
            label = { Text("Монгол утга", color = Color.White) },
            placeholder = { Text("жишээ: ногоон", color = Color.LightGray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF6200EE),
                focusedBorderColor = Color(0xFF6200EE),
                cursorColor = Color.Black,
                unfocusedLabelColor = Color.White,
                focusedLabelColor = Color.White,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            PurpleButton(
                text = if (wordId == -1) "ОРУУЛАХ" else "ХАДГАЛАХ"
            ) {
                if (eng.isNotBlank() && mon.isNotBlank()) {
                    if (wordId == -1) {
                        // шинэ үг
                        vm.addWord(eng.trim(), mon.trim())
                    } else {
                        // байгаа үгийг шинэчлэх
                        vm.updateWord(wordId, eng.trim(), mon.trim())
                    }
                    onDone()
                }
            }

            PurpleButton("БОЛИХ") {
                onDone()
            }
        }
    }
}
