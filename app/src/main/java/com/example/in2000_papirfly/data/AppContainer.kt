package com.example.in2000_papirfly.data
import com.example.in2000_papirfly.ui.viewmodels.CustomizationViewModel
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel

class AppContainer {

    // Repositories
    val weatherRepository = WeatherRepositoryMVP()
    val loadoutRepository = LoadoutRepository()

    // ViewModel Factories
    val customizationViewModelFactory = CustomizationViewModel.Factory


}