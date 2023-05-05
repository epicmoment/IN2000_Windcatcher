package com.example.in2000_papirfly.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun NavScreen(
    viewModel : ScreenStateViewModel = viewModel()
) {

    val screenState = viewModel.screenState.collectAsState()
    val navController = rememberNavController()

     NavHost(
        navController = navController,
        startDestination = "MainScreen"
    ) {

         composable(route = "MainScreen") {
            MainScreen(
//                onNextPage = { navController.navigate("PositionScreen") },
                onNextPage = { navController.navigate("ThrowScreen") },
                onCustomizePage = { navController.navigate("CustomizationScreen")}
            )
         }

         composable(route = "PositionScreen") {

            PositionScreen(
                onNextPage = { newLocation, locationName ->
                    viewModel.setLocation(newLocation, locationName)
                    navController.navigate("ThrowScreen")
                }
            )

        }

        composable(route = "ThrowScreen") {
            val pos = GeoPoint(
                screenState.value.location.latitude,
                screenState.value.location.longitude
            )
            ThrowScreen(
                selectedLocation = pos,
                locationName = screenState.value.locationName,
                onBack = {
                    navController.popBackStack(
                        route = "MainScreen",
                        inclusive = false
                    )
                }
            )

        }

         composable(route = "CustomizationScreen") {
             CustomizationScreen(
                 onNextPage = {
                     navController.popBackStack(
                         route = "MainScreen",
                         inclusive = false
                     )
                 }
             )
         }

    }

}