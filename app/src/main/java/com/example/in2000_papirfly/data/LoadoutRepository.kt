package com.example.in2000_papirfly.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoadoutRepository {


    private val _loadoutState = MutableStateFlow(Loadout())
    val loadoutState : StateFlow<Loadout> = _loadoutState.asStateFlow()

    fun equipAttachment(slot : Int, attachmentID : Int?) {

        _loadoutState.update {

            when (slot) {
                1 -> it.copy(slot1attachment = attachmentID)
                2 -> it.copy(slot2attachment = attachmentID)
                3 -> it.copy(slot3attachment = attachmentID)
                else -> it.copy(slot4attachment = attachmentID)
            }

        }

    }

}