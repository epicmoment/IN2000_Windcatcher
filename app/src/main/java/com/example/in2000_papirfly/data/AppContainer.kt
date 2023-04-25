package com.example.in2000_papirfly.data

import android.content.Context
import com.example.in2000_papirfly.data.database.PapirflyDatabase
import com.example.in2000_papirfly.ui.viewmodels.ScreenStateViewModel

class AppContainer(database: PapirflyDatabase) {

    val weatherRepository = WeatherRepositoryMVP()
    val dataRepository = DataRepository(database)
    val screenStateViewModelFactory = ScreenStateViewModel.Factory

}