package com.example.in2000_papirfly.ui.viewmodels

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.in2000_papirfly.data.Location
import com.example.in2000_papirfly.data.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class ScreenStateViewModel (
) : ViewModel() {

    private val _screenState = MutableStateFlow(ScreenState())
    val screenState : StateFlow<ScreenState> = _screenState.asStateFlow()

    fun setLocation(newLocation : GeoPoint) {

        viewModelScope.launch {
            _screenState.update {

                it.copy(location = newLocation)

            }
        }
    }

}
