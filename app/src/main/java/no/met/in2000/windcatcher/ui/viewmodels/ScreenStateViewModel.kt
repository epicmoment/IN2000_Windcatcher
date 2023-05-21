package no.met.in2000.windcatcher.ui.viewmodels

import androidx.lifecycle.*
import no.met.in2000.windcatcher.data.screenuistates.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class ScreenStateViewModel: ViewModel() {

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
}
