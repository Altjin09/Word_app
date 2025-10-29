package com.example.words.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.words.ui.screens.EditScreen
import com.example.words.ui.screens.HomeScreen
import com.example.words.ui.screens.SettingsScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val vm: MainViewModel = viewModel(factory = MainViewModelFactory(navController.context))

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // HOME
        composable("home") {
            HomeScreen(
                vm = vm,
                onAdd = {
                    // шинээр нэмэх горим -> id = -1, хоосон талбарууд
                    navController.navigate("edit/-1//")
                },
                onEdit = {
                    // засварлах горим -> одоогийн үгийг vm-ээс авч route-рүү шиднэ
                    val current = vm.ui.value.words.getOrNull(vm.ui.value.currentIndex)
                    if (current != null) {
                        // eng/mon талд / байгаа бол навигацын зам эвдэрнэ,
                        // энгийн шийдэл болгон тэдгээрийг encode хийе
                        val engSafe = java.net.URLEncoder.encode(current.english, "UTF-8")
                        val monSafe = java.net.URLEncoder.encode(current.mongolian, "UTF-8")
                        navController.navigate("edit/${current.id}/$engSafe/$monSafe")
                    } else {
                        // үг байхгүй үед зүгээр add screen рүү орно
                        navController.navigate("edit/-1//")
                    }
                },
                onSettings = {
                    navController.navigate("settings")
                }
            )
        }

        // EDIT (нэмэх болон засах аль алинд ашиглана)
        composable(
            route = "edit/{id}/{eng}/{mon}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("eng") { type = NavType.StringType; defaultValue = "" },
                navArgument("mon") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getInt("id") ?: -1
            val engArg = backStackEntry.arguments?.getString("eng") ?: ""
            val monArg = backStackEntry.arguments?.getString("mon") ?: ""

            // decode буцааж авна (нэгэнт encode хийсэн болохоор)
            val engDecoded = java.net.URLDecoder.decode(engArg, "UTF-8")
            val monDecoded = java.net.URLDecoder.decode(monArg, "UTF-8")

            EditScreen(
                vm = vm,
                wordId = idArg,
                initialEng = engDecoded,
                initialMon = monDecoded,
                onDone = { navController.popBackStack() }
            )
        }

        // SETTINGS
        composable("settings") {
            SettingsScreen(
                vm = vm,
                onClose = { navController.popBackStack() }
            )
        }
    }
}
