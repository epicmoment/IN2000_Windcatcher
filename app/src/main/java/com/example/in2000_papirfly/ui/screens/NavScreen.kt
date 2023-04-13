package com.example.in2000_papirfly.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.in2000_papirfly.data.PlaneRepository
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel
import org.osmdroid.util.GeoPoint
import com.example.in2000_papirfly.data.WeatherRepositoryMVP
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.in2000_papirfly.PapirflyApplication

@Composable
fun NavScreen(viewModel : ScreenStateViewModel = viewModel()) {

    // { ScreenStateViewModelFactory("tekst1") }
    // val viewModel: ScreenStateViewModel = ViewModelProvider(ScreenStateViewModelFactory("tekst1"),   )
    val screenState = viewModel.screenState.collectAsState()

    val repository : WeatherRepositoryMVP = viewModel()
    val planeRepository = PlaneRepository()     // TODO // Burde bli flyttet, dependency injection

    val navController = rememberNavController()

    val appContainer = (LocalContext.current.applicationContext as PapirflyApplication).appContainer

    NavHost(
        navController = navController,
        startDestination = "MainScreen",
        modifier = Modifier.padding(8.dp)
    ) {

        composable(route = "MainScreen") {
            MainScreen {
                navController.navigate("PositionScreen")
            }
        }

        composable(route = "PositionScreen") {

            PositionScreen(
                onNextPage = { newLocation ->
                    viewModel.setLocation(newLocation)
                    navController.navigate("ThrowScreen")
                },

                getWeather = { location: String ->
                    repository.getWeatherAt(location)
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
                getWeather = { location: String ->
                    repository.getWeatherAt(location)
                },
                weatherRepository = repository,
                planeRepository = planeRepository
            )

        }

    }

}