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

object Storage : Callback {
    var processDialogs = false
    var processMessages = false
    var dialogSubscriber : Callback? = null
    var messageSubscriber : Callback? = null

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
            1 -> {
                if (!processDialogs) {
                    processDialogs = true
                    dialogSubscriber?.onLoad(Response(3, API.api[arg.response]!!.getDialogs(TUID)))
                }
            }
            2 -> {

            }
        }
    }
}