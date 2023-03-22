package com.example.in2000_papirfly.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class PositionScreenViewModel: ViewModel() {
    //må ha PositionScreenUiState inni <>
    //PositionsScreenUiState må lages
    private var _posScrUiState: MutableStateFlow<> = MutableStateFlow()
    var posScrUiState: StateFlow<> = _posScrUiState.asStateFlow()

    init {
        posScreenView()
    }

    private fun posScreenView() {
        viewModelScope.launch {
            try {
                _posScrUiState.update { currentState ->
                    currentState.copy(

                    )
                }
            } catch (e: IOException) {
                Log.e("API", "Error ${e}")
            }
        }
    }
}