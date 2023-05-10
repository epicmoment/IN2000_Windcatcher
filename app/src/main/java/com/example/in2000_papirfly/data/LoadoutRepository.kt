package com.example.in2000_papirfly.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoadoutRepository {


    private val _loadoutState = MutableStateFlow(Loadout())
    val loadoutState : StateFlow<Loadout> = _loadoutState.asStateFlow()

    fun equipAttachment(slot : Int, attachmentID : Int) {

        val list2 = loadoutState.value.slots.toMutableList()
        list2[slot] = attachmentID

        _loadoutState.update {

            it.copy(
                slots = list2.toList()
            )

        }

    }

    fun getAttachmentInSlot(slot : Int) : Attachment {
        return Attachments.list[slot][loadoutState.value.slots[slot]]
    }

}