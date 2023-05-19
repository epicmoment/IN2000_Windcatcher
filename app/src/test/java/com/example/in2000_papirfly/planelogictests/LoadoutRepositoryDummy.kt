package com.example.in2000_papirfly.planelogictests

import com.example.in2000_papirfly.R
import com.example.in2000_papirfly.data.components.Attachment
import com.example.in2000_papirfly.data.components.LoadOut
import com.example.in2000_papirfly.data.repositories.LoadOutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoadOutRepositoryDummy: LoadOutRepository {
    val loadOut = LoadOut()
    override val loadOutState: StateFlow<LoadOut> = MutableStateFlow(LoadOut()).asStateFlow()
    override fun equipAttachment(slot: Int, attachmentID: Int) {

    }

    override fun getAttachmentInSlot(slot: Int): Attachment {
        return Attachment(R.string.papertype_01, R.string.papertype_02)
    }
}