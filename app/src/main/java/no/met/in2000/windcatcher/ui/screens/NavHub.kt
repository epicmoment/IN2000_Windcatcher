package no.met.in2000.windcatcher.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.met.in2000.windcatcher.ui.viewmodels.ScreenStateViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun NavHub(
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
                onNextPage = { navController.navigate("ThrowScreen") },
                onCustomizePage = { navController.navigate("CustomizationScreen") }
            )
         }

        composable(route = "ThrowScreen") {
            ThrowScreen(
                selectedLocation = screenState.value.location,
                locationName = screenState.value.locationName,
                changeLocation = {
                        locationPoint: GeoPoint, locationName: String ->
                            viewModel.setLocation(
                                locationPoint, locationName
                            )
                        },
                onCustomizePage = { navController.navigate("CustomizationScreen") },
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
                         route = "CustomizationScreen",
                         inclusive = true
                     )
                 }
             )
         }
    }
}