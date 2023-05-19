package com.example.in2000_papirfly.data.repositories

import com.example.in2000_papirfly.data.components.Attachment
import com.example.in2000_papirfly.data.components.Attachments
import com.example.in2000_papirfly.data.components.LoadOut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


interface LoadOutRepository{
    val loadOutState: StateFlow<LoadOut>

    fun equipAttachment(slot: Int, attachmentID: Int)
    fun getAttachmentInSlot(slot : Int) : Attachment
}
class LoadOutRepo: LoadOutRepository {


    private val _loadOutState = MutableStateFlow(LoadOut())
    override val loadOutState : StateFlow<LoadOut> = _loadOutState.asStateFlow()

    override fun equipAttachment(slot : Int, attachmentID : Int) {

        val list2 = loadOutState.value.slots.toMutableList()
        list2[slot] = attachmentID

        _loadOutState.update {

            it.copy(
                slots = list2.toList()
            )

        }

    }

    override fun getAttachmentInSlot(slot : Int) : Attachment {
        return Attachments.list[slot][loadOutState.value.slots[slot]]
    }

}