package com.example.in2000_papirfly.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.DataRepository
import com.example.in2000_papirfly.data.PositionScreenUiState
import com.example.in2000_papirfly.data.WeatherRepositoryDummy
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.IOException

class PositionScreenViewModel(val repo: DataRepository): ViewModel() {

    private var _posScrUiState: MutableStateFlow<PositionScreenUiState> =
        MutableStateFlow(PositionScreenUiState(repo.getThrowPointWeatherList()))
    var posScrUiState: StateFlow<PositionScreenUiState> = _posScrUiState.asStateFlow()

    init {
        posScreenView()
    }

    private fun posScreenView() {
        viewModelScope.launch {
            try {
//                val weatherRepo = list
                _posScrUiState.update { currentState ->
                    currentState.copy(
                        weather = repo.getThrowPointWeatherList()
                    )
                }
            } catch (e: Throwable) {
                Log.e("API", "Error ${e}")
            }
        }
    }
}