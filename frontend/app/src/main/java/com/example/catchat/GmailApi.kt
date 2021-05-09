package com.example.catchat


import Common
import GmailApiGrpc
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.models.Dialog
import com.example.models.Message
import com.example.myapplication.ui.home.Api
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.net.URL
import java.util.logging.Logger
import java.util.stream.Collectors

class GmailApi : Api {
    private val logger = Logger.getLogger(this.javaClass.name)
    private val stub = GmailApiGrpc.newBlockingStub(channel())

    private fun channel(): ManagedChannel {
        val url = URL("http://84.252.137.106:6066")
        val port = if (url.port == -1) url.defaultPort else url.port

        logger.info("Connecting to ${url.host}:$port")

        val builder = ManagedChannelBuilder.forAddress(url.host, port)
        if (url.protocol == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        return builder.executor(Dispatchers.Default.asExecutor()).build()
    }

    override fun sendPhone(uid: String, phone: String): String {
        TODO("Not yet implemented")
    }

    override fun sendCode(uid: String, phone: String, code: String, codeHash: String) {
        TODO("Not yet implemented")
    }

    override fun sendMarkRead(uid: String, dialogId: Long) {
        TODO("Not yet implemented")
    }

    override fun sendTextMessage(uid: String, dialogId: Long, text: String) {
        TODO("Not yet implemented")
    }

    override fun getDialogs(
        uid: String
    ) : List<Dialog> {


        val request = Common.User.newBuilder().setUid(uid).build()
        val response = stub.getDialogs(request)
        return response.dialogList.stream().map { d -> Dialog(d.name, d.message, d.date, d.unreadCount, d.dialogId) }.collect(Collectors.toList())
    }

    override fun getMessages(uid: String, dialogId: Long): List<Message> {
        val request = Common.DialogRequest.newBuilder().setUid(uid).setDialogId(dialogId).build()
        val response = stub.getMessages(request)
        return response.messageList.stream().map { d -> Message("", d.message, d.sender == "me") }.filter { it.text.length > 0 }.collect(Collectors.toList())

    }
}
