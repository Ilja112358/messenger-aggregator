package com.aggregator.api


import Tg
import TgApiGrpc
import com.aggregator.models.Dialog
import com.aggregator.models.Message
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import java.util.stream.Collectors


class TgApi : Api {
    private val logger = Logger.getLogger(this.javaClass.name)
    private val stub = TgApiGrpc.newBlockingStub(channel())
    private val stubGmail = GmailApiGrpc.newBlockingStub(channel())

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

    override fun sendPhone(
        uid: String,
        phone: String
    ): String {
        val request = Tg.TgAuthRequest.newBuilder().setUid(uid).setPhone(phone).build()
        val response = stub.auth(request)

        return response.data
    }

    override fun sendCode(
        uid: String,
        phone: String,
        code: String,
        codeHash: String
    ) {
        val request = Tg.TgAuthRequest.newBuilder().setUid(uid).setPhone(phone).setCode(code).setCodeHash(codeHash).build()
        val response = stub.auth(request)
    }

    override fun sendMarkRead(uid : String, dialogId: String) {
        val request = Common.DialogRequest.newBuilder().setDialogId(dialogId.toLong()).setUid(uid).build()
        val response = stub.markRead(request)
        println(response.status)
    }

    override fun sendTextMessage(uid: String, dialogId: String, text: String) {
        val request = Common.Send.newBuilder().setUid(uid).setDialogId(dialogId.toLong()).setMessage(text).build()
        val response = stub.sendMessage(request)
    }

    fun getUsernameById(uid: String, dialogId: String): String {
        val request = Common.UserId.newBuilder().setUid(uid).setId(dialogId.toLong()).build()
        val response = stub.getUsernameById(request)
        return response.username
    }

    fun getIdByUsername(uid: String, username: String): String {
        val request = Common.UserName.newBuilder().setUid(uid).setUsername(username).build()
        val response = stub.getIdByUsername(request)
        return response.id.toString()
    }

    override fun getDialogs(
        uid: String
    ) : List<Dialog> {
        val request = Common.User.newBuilder().setUid(uid).build()
        val response = stub.getDialogs(request)
        //val parser =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss+00:00")
        return response.dialogList.stream().map { d -> Dialog(d.name, d.message, getShortDate(Date(d.date * 1000)), d.unreadCount, d.dialogId.toString(), d.avatarUrl) }.collect(Collectors.toList())
    }

    override fun getMessages(
        uid: String,
        dialogId: String
    ) : List<Message> {
        val request = Common.DialogRequest.newBuilder().setUid(uid).setDialogId(dialogId.toLong()).build()
        val response = stub.getMessages(request)
        val result = response.messageList.stream().map { d -> Message(d.sender, d.message, getTime(d.date), d.sender == "me", d.date, d.attachment.type, d.attachment.url) }.filter { it.attachementType.length >0 || it.text.length > 0 }.collect(Collectors.toList())
        return result
    }

    private fun getTime(dateRaw: Long) : String {
        val date = Date(dateRaw)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
        cal.time = date

        return cal[Calendar.HOUR_OF_DAY].toString() + ":" + cal[Calendar.MINUTE].toString().padStart(2, '0')
    }

    private fun getShortDate(date: Date?): String {
        if (date == null) {
            return  ""
        }

        val curDate = Calendar.getInstance().time

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
        cal.time = date

        val calCur = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
        calCur.time = curDate

        var result: String = ""
        if (cal[Calendar.YEAR] == calCur[Calendar.YEAR] && cal[Calendar.MONTH] == calCur[Calendar.MONTH] && cal[Calendar.DAY_OF_MONTH] == calCur[Calendar.DAY_OF_MONTH]) {
            result = cal[Calendar.HOUR_OF_DAY].toString() + ":" + cal[Calendar.MINUTE].toString().padStart(2, '0')
        } else if (cal[Calendar.YEAR]  == calCur[Calendar.YEAR]) {
            result = cal[Calendar.DAY_OF_MONTH].toString().padStart(2, '0') + "." + (cal[Calendar.MONTH]+1).toString().padStart(2, '0')
        } else {
            result = cal[Calendar.DAY_OF_MONTH].toString().padStart(2, '0') + "." + (cal[Calendar.MONTH]+1).toString().padStart(2, '0') + "." + cal[Calendar.YEAR].toString().takeLast(2)
        }
        return result
    }
}
