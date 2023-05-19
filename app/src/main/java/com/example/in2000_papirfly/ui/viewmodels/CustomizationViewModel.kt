package com.example.in2000_papirfly.ui.viewmodels

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.in2000_papirfly.PapirflyApplication
import com.example.in2000_papirfly.data.*
import com.example.in2000_papirfly.data.repositories.LoadOutRepository
import com.example.in2000_papirfly.data.screenuistates.CustomizationScreenUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomizationViewModel (
    val loadOutRepository: LoadOutRepository,
) : ViewModel() {

    private val _customizeState = MutableStateFlow(CustomizationScreenUIState())
    val customizeState : StateFlow<CustomizationScreenUIState> = _customizeState.asStateFlow()

    val loadOutState = loadOutRepository.loadOutState

    fun setSlot(newSlot : Int) {

        viewModelScope.launch {
            _customizeState.update {

                it.copy(selectedSlot = newSlot)

            }

        }
    }

    fun equipAttachment(slot : Int, attachmentID : Int) {

        viewModelScope.launch {

            loadOutRepository.equipAttachment(slot, attachmentID)
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
                    (application as PapirflyApplication).appContainer.loadOutRepository,
                ) as T
            }
        }
    }

}
