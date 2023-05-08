package com.example.in2000_papirfly.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.addAttachments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomizationViewModel (
    val loadoutRepository: LoadoutRepository,
    val planeRepository: PlaneRepository,
) : ViewModel() {

    private val _customizeState = MutableStateFlow(CustomizationScreenUIState())
    val customizeState : StateFlow<CustomizationScreenUIState> = _customizeState.asStateFlow()

    val loadoutState = loadoutRepository.loadoutState

    fun setSlot(newSlot : Int) {

        viewModelScope.launch {
            _customizeState.update {

                it.copy(selectedSlot = newSlot)

            }

        }
    }

    fun equipAttachment(slot : Int, attachmentID : Int) {

        viewModelScope.launch {

            loadoutRepository.equipAttachment(slot, attachmentID)
            addAttachments(planeRepository = planeRepository, loadoutRepository = loadoutRepository)
        }

    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {

                val application = checkNotNull(extras[APPLICATION_KEY])

                return CustomizationViewModel(
                    (application as PapirflyApplication).appContainer.loadoutRepository,
                    (application as PapirflyApplication).appContainer.planeRepository,
                ) as T
            }
        }
    }

}
