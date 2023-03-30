package com.example.in2000_papirfly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.PositionScreenUiState
import com.example.in2000_papirfly.data.WeatherRepositoryDummy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.IOException

val list = listOf<WeatherRepositoryDummy>(WeatherRepositoryDummy(
        namePos = "Oslo", geoPoint = GeoPoint(59.944030, 10.719282)),
    WeatherRepositoryDummy(
        namePos = "Stavanger", geoPoint = GeoPoint(58.89729, 5.71185)),
    WeatherRepositoryDummy(
        namePos = "Galdhøpiggen", geoPoint = GeoPoint(61.63681, 8.31250))
)

class PositionScreenViewModel: ViewModel() {
    private var _posScrUiState: MutableStateFlow<PositionScreenUiState> = MutableStateFlow(PositionScreenUiState(list)
    )
    var posScrUiState: StateFlow<PositionScreenUiState> = _posScrUiState.asStateFlow()

    init {
        posScreenView()
    }

    private fun posScreenView() {
        viewModelScope.launch {
            try {
                val weatherRepo = list
                _posScrUiState.update { currentState ->
                    currentState.copy(
                        weather = weatherRepo
                    )
                }
            } catch (e: IOException) {
                Log.e("API", "Error ${e}")
            }
        }
    }
}