package com.example.words.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.words.ui.MainViewModel

@Composable
fun SettingsScreen(
    vm: MainViewModel,
    onClose: () -> Unit
) {
    val state by vm.ui.collectAsState()
    var selected by remember(state.mode) { mutableStateOf(state.mode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = selected == 1,
                onClick = { selected = 1 }
            )
            Text(
                text = "Гадаад үгийг нь харуулаад Монгол утгыг асууна",
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = selected == 2,
                onClick = { selected = 2 }
            )
            Text(
                text = "Монгол үгийг нь харуулаад Гадаад утгыг асууна",
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = selected == 3,
                onClick = { selected = 3 }
            )
            Text(
                text = "Хоёуланг нь асууна (random)",
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PurpleButton("БУЦАХ") { onClose() }
            PurpleButton("ХАДГАЛАХ") {
                vm.setMode(selected)
                onClose()
            }
        }
    }
}
