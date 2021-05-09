package com.aggregator.store

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aggregator.api.API
import com.aggregator.api.GmailApi
import com.aggregator.api.TgApi
import com.aggregator.models.Dialog
import com.aggregator.ui.fragments.TUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

interface Callback {
    fun onLoad(arg: Response<*>)
}

class Response<T>(val type: Int, val response: T)
object RespType {
    const val getDIALOGS = 1
    const val getMESSAGES = 2
    const val DIALOGS = 3
    const val MESSAGES = 4
}

object Storage : Callback {
    var processDialogs = false
    var processMessages = false
    var dialogSubscriber : Callback? = null
    var messageSubscriber : Callback? = null
    private val getter = DialogsGetter()

    fun subscribe(sub: Callback) {
        when (sub) {
            is Fragment -> dialogSubscriber = sub
            is Activity -> messageSubscriber = sub
        }
    }

    fun unsubscribe(sub: Callback) {
        when (sub) {
            is Fragment -> dialogSubscriber = null
            is Activity -> messageSubscriber = null
        }
    }

    override fun onLoad(arg: Response<*>) = runBlocking {
        when (arg.type) {
            RespType.getDIALOGS -> {
                if (!processDialogs && dialogSubscriber != null) {
                    processDialogs = true
                    getter.setDialogs(arg.response as String)
                }
            }
            RespType.getMESSAGES -> {

            }
        }
    }
}

class DialogsGetter : ViewModel() {
    fun setDialogs(apiType: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                API.api[apiType]!!.getDialogs(TUID)
            }
            Storage.dialogSubscriber?.onLoad(Response(RespType.DIALOGS, result))
            Storage.processDialogs = false
        }
    }
}