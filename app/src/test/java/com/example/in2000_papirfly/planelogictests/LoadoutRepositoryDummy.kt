package com.example.in2000_papirfly.planelogictests

import com.example.in2000_papirfly.data.Attachment
import com.example.in2000_papirfly.data.Loadout
import com.example.in2000_papirfly.data.LoadoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoadoutRepositoryDummy: LoadoutRepository {
    val loadout = Loadout()
    override val loadoutState: StateFlow<Loadout> = MutableStateFlow(Loadout()).asStateFlow()
    override fun equipAttachment(slot: Int, attachmentID: Int) {

    }

    override fun getAttachmentInSlot(slot: Int): Attachment {
        return Attachment("Test attachment", "Description")
    }
}