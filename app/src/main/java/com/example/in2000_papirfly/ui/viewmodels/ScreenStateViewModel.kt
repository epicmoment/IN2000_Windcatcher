package com.example.in2000_papirfly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000_papirfly.data.Location
import com.example.in2000_papirfly.data.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScreenStateViewModel : ViewModel() {

    private val _screenState = MutableStateFlow(ScreenState())
    val screenState : StateFlow<ScreenState> = _screenState.asStateFlow()


    fun setLocation(newLocation : Location) {

        viewModelScope.launch {
            _screenState.update {

                it.copy(location = newLocation)

            }
        }



    }



}
