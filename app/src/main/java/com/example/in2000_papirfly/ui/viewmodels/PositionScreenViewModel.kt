package com.example.in2000_papirfly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.Location
import com.example.in2000_papirfly.data.PositionScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
class PositionScreenViewModel: ViewModel() {
    private var _posScrUiState: MutableStateFlow<PositionScreenUiState> = MutableStateFlow(PositionScreenUiState())
    var posScrUiState: StateFlow<PositionScreenUiState> = _posScrUiState.asStateFlow()

    init {
        posScreenView()
    }

    private fun posScreenView() {
        viewModelScope.launch {
            try {
                //
                //val weatherRepo = getWeatherAt(Location())
                _posScrUiState.update { currentState ->
                    currentState.copy(
                        //weather = weatherRepo
                    )
                }
            } catch (e: IOException) {
                Log.e("API", "Error ${e}")
            }
        }
    }
}