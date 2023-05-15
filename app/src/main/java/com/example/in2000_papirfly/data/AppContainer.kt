package com.example.in2000_papirfly.data

import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.ui.viewmodels.CustomizationViewModel
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModelFactory


class AppContainer(database: PapirflyDatabase) {

    // Repositories
    val loadoutRepository: LoadoutRepository = LoadoutRepo()
    val dataRepository = DataRepository(database)
    val planeRepository: PlaneRepository = PlaneRepo()


    // ViewModel Factories
    val customizationViewModelFactory = CustomizationViewModel.Factory
    val throwViewModelFactory = ThrowViewModelFactory(
        weatherRepository = dataRepository,
        planeRepository = planeRepository,
        loadoutRepository = loadoutRepository,
    )
}