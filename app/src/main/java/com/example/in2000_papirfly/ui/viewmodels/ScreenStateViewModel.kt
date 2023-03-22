package com.example.in2000_papirfly.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.in2000_papirfly.data.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScreenStateViewModel : ViewModel() {

    /*enum class ScreenChoice {
        LocationSelect,
        PlaneLaunchScreen

    }*/

    private val _screenState = MutableStateFlow(ScreenState())
    val screenState : StateFlow<ScreenState> = _screenState.asStateFlow()





}
