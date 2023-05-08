package com.example.in2000_papirfly.data

import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.ui.viewmodels.CustomizationViewModel
import com.example.in2000_papirfly.ui.viewmodels.PositionScreenViewModelFactory
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModelFactory


class AppContainer(database: PapirflyDatabase) {

    // Repositories
    val loadoutRepository = LoadoutRepository()
    val dataRepository = DataRepository(database)
    val planeRepository = PlaneRepository()


    // ViewModel Factories
    val customizationViewModelFactory = CustomizationViewModel.Factory
    val throwViewModelFactory = ThrowViewModelFactory(
        weatherRepository = dataRepository,
        planeRepository = planeRepository
    )
    val positionScreenViewModelFactory = PositionScreenViewModelFactory(dataRepository)

}