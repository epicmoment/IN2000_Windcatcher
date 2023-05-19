package com.example.in2000_papirfly.data

import com.example.in2000_papirfly.data.database.DataBaseContentNegotiator
import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.data.repositories.LoadOutRepository
import com.example.in2000_papirfly.data.repositories.LoadOutRepo
import com.example.in2000_papirfly.data.repositories.PlaneRepo
import com.example.in2000_papirfly.data.repositories.PlaneRepository
import com.example.in2000_papirfly.ui.viewmodels.CustomizationViewModel
import com.example.in2000_papirfly.ui.viewmodels.ThrowViewModelFactory


class AppContainer(database: PapirflyDatabase) {

    // Repositories
    val loadOutRepository: LoadOutRepository = LoadOutRepo()
    val dataBaseContentNegotiator = DataBaseContentNegotiator(database)
    val planeRepository: PlaneRepository = PlaneRepo()


    // ViewModel Factories
    val customizationViewModelFactory = CustomizationViewModel.Factory
    val throwViewModelFactory = ThrowViewModelFactory(
        weatherRepository = dataBaseContentNegotiator,
        planeRepository = planeRepository,
        loadOutRepository = loadOutRepository,
    )
}