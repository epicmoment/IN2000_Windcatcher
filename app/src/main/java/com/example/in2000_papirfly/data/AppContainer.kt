package com.example.in2000_papirfly.data

import android.content.Context
import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel
import com.example.in2000_papirfly.ui.viewmodels.CustomizationViewModel


class AppContainer(database: PapirflyDatabase) {

    // Repositories
    val weatherRepository = WeatherRepositoryMVP()
    val loadoutRepository = LoadoutRepository()
    val dataRepository = DataRepository(database)

    // ViewModel Factories
    val customizationViewModelFactory = CustomizationViewModel.Factory

}