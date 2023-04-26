package com.example.in2000_papirfly.ui.viewmodels

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.in2000_papirfly.data.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class ScreenStateViewModel (
    val parameter1 : String,
    val savedStateHandle: SavedStateHandle // Trenger vi denne til noe?
) : ViewModel() {

    private val _screenState = MutableStateFlow(ScreenState())
    val screenState : StateFlow<ScreenState> = _screenState.asStateFlow()

    fun setLocation(newLocation : GeoPoint, locationName: String) {

        viewModelScope.launch {
            _screenState.update {

                it.copy(
                    location = newLocation,
                    locationName = locationName
                )

            }
        }
    }

    fun getParam() : String {
        return parameter1
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {

                val savedStateHandle = extras.createSavedStateHandle()

                return ScreenStateViewModel(
                    "tekst1",
                    savedStateHandle
                ) as T
            }
        }
    }

}
