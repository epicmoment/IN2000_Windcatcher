package no.met.in2000.windcatcher.planelogictests

import no.met.in2000.windcatcher.R
import no.met.in2000.windcatcher.data.components.Attachment
import no.met.in2000.windcatcher.data.components.LoadOut
import no.met.in2000.windcatcher.data.repositories.LoadOutRepository
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