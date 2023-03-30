package com.example.in2000_papirfly.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel

@Composable
fun NavScreen(viewModel : ScreenStateViewModel = viewModel()) {

    val screenState = viewModel.screenState.collectAsState()

    val navController = rememberNavController()

    NavHost (
        navController = navController,
        startDestination = "MainScreen",
        modifier = Modifier.padding(8.dp)
    ) {

        composable(route = "MainScreen") {
            MainScreen {
                navController.navigate("ThrowScreen")
            }
        }

        composable(route = "PositionScreen") {

            PositionScreen { newLocation ->

                viewModel.setLocation(newLocation)
                navController.navigate("ThrowScreen")

            }

        }

        composable(route = "ThrowScreen") {

            ThrowScreen(screenState.value.location)

        }

    }

}