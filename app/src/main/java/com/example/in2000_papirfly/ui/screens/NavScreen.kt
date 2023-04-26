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
import com.example.in2000_papirfly.data.PlaneRepository
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel
import org.osmdroid.util.GeoPoint
import androidx.compose.ui.platform.LocalContext
import com.example.in2000_papirfly.PapirflyApplication

@Composable
fun NavScreen(
    viewModel : ScreenStateViewModel = viewModel()
) {

    val screenState = viewModel.screenState.collectAsState()

//    val repository : WeatherRepositoryMVP = viewModel()
    val planeRepository = PlaneRepository()     // TODO // Burde bli flyttet, dependency injection

    val navController = rememberNavController()

    val appContainer = (LocalContext.current.applicationContext as PapirflyApplication).appContainer
    val repository = appContainer.dataRepository

     NavHost(
        navController = navController,
        startDestination = "MainScreen"
    ) {

         composable(route = "MainScreen") {
            MainScreen(
                onNextPage = { navController.navigate("PositionScreen") },
                onCustomizePage = { navController.navigate("CustomizationScreen")}
            )
         }

         composable(route = "PositionScreen") {

            PositionScreen(
                onNextPage = { newLocation, locationName ->
                    viewModel.setLocation(newLocation, locationName)
                    navController.navigate("ThrowScreen")
                },
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
                getWeather = { location: String ->
                    repository.getWeatherAt(location)
                },
                weatherRepository = repository,
                planeRepository = planeRepository
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