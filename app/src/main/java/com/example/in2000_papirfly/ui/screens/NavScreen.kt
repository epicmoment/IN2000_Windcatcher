package com.example.in2000_papirfly.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.in2000_papirfly.ui.screens.PositionScreen
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel

@Composable
fun NavScreen(viewModel : ScreenStateViewModel = viewModel()) {

    val navController = rememberNavController()

    NavHost (
        navController = navController,
        startDestination = "PositionScreen",
        modifier = Modifier.padding(8.dp)
    ) {

        composable(route = "PositionScreen") {
            PositionScreen()
        }

        composable(route = "ThrowScreen") {
            ThrowScreen()
        }


    }


}