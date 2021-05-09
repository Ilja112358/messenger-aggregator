package com.aggregator.api


import Common
import GmailApiGrpc
import com.aggregator.models.Dialog
import com.aggregator.models.Message
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.net.URL
import java.util.*
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

    override fun sendMarkRead(uid: String, dialogId: String) {
        TODO("Not yet implemented")
    }

    override fun sendTextMessage(uid: String, dialogId: String, text: String) {
        val request = Common.Send.newBuilder().setUid(uid).setThreadId(dialogId).setMessage(text).build()
        val response = stub.sendMessage(request)
    }

    override fun getDialogs(
        uid: String
    ) : List<Dialog> {


        val request = Common.User.newBuilder().setUid(uid).build()
        val response = stub.getDialogs(request)
        return response.dialogList.stream().map { d -> Dialog(d.name, d.message, "", d.unreadCount, d.threadId, d.avatarUrl) }.collect(Collectors.toList())
    }

    override fun getMessages(uid: String, dialogId: String): List<Message> {
        val request = Common.DialogRequest.newBuilder().setUid(uid).setThreadId(dialogId).build()
        val response = stub.getMessages(request)
        return response.messageList.stream().map { d -> Message(d.sender, d.message, getTime(d.date), d.sender == "me", d.date, d.attachment.type, d.attachment.url) }.filter { it.text.length > 0 }.collect(Collectors.toList())

    }

    private fun getTime(dateRaw: Long) : String {
        val date = Date(dateRaw)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
        cal.time = date

        return cal[Calendar.HOUR_OF_DAY].toString() + ":" + cal[Calendar.MINUTE].toString().padStart(2, '0')
    }
}
