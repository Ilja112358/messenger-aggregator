package com.aggregator.api


import com.aggregator.models.Dialog
import com.aggregator.models.Message

interface Api {
    fun sendPhone(uid: String, phone: String): String
    fun sendCode(uid: String, phone: String, code: String, codeHash: String)
    fun sendMarkRead(uid : String, dialogId: String)
    fun sendTextMessage(uid: String, dialogId: String, text: String)
    fun getDialogs(uid: String) : List<Dialog>
    fun getMessages(uid: String, dialogId: String) : List<Message>
}
