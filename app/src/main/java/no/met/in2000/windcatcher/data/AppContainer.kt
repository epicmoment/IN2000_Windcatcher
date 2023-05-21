package no.met.in2000.windcatcher.data

import no.met.in2000.windcatcher.data.database.DataBaseContentNegotiator
import no.met.in2000.windcatcher.data.database.WindcatcherDatabase
import no.met.in2000.windcatcher.data.repositories.LoadOutRepository
import no.met.in2000.windcatcher.data.repositories.LoadOutRepo
import no.met.in2000.windcatcher.data.repositories.PlaneRepo
import no.met.in2000.windcatcher.data.repositories.PlaneRepository
import no.met.in2000.windcatcher.ui.viewmodels.CustomizationViewModel
import no.met.in2000.windcatcher.ui.viewmodels.ThrowViewModelFactory


class AppContainer(database: WindcatcherDatabase) {

    // Repositories
    val loadOutRepository: LoadOutRepository = LoadOutRepo()
    private val dataBaseContentNegotiator = DataBaseContentNegotiator(database)
    private val planeRepository: PlaneRepository = PlaneRepo()


    // ViewModel Factories
    val customizationViewModelFactory = CustomizationViewModel.Factory
    val throwViewModelFactory = ThrowViewModelFactory(
        weatherRepository = dataBaseContentNegotiator,
        planeRepository = planeRepository,
        loadOutRepository = loadOutRepository,
    )
}