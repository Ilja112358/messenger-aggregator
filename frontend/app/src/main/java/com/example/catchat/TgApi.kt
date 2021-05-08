package com.example.catchat


import Tg
import TgApiGrpc
import com.example.models.Dialog
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import java.util.stream.Collectors


val tgApi = TgApi()

class TgApi {
    private val logger = Logger.getLogger(this.javaClass.name)
    private val stub = TgApiGrpc.newBlockingStub(channel())

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

    fun sendPhone(
        uid: String,
        phone: String
    ): String {
        val request = Tg.AuthRequest.newBuilder().setUid(uid).setPhone(phone).build()
        val response = stub.auth(request)

        return response.data
    }

    fun sendCode(
        uid: String,
        phone: String,
        code: String,
        codeHash: String
    ) {
        val request = Tg.AuthRequest.newBuilder().setUid(uid).setPhone(phone).setCode(code).setCodeHash(codeHash).build()
        val response = stub.auth(request)
    }

    fun getDialogs(
        uid: String
    ) : List<Dialog> {
        val request = Tg.User.newBuilder().setUid(uid).build()
        val response = stub.getDialogs(request)
        val parser =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss+00:00")
        return response.dialogList.stream().map { d -> Dialog(d.name, d.message, getShortDate(parser.parse(d.date)) ) }.collect(Collectors.toList())
    }

    private fun getShortDate(date: Date?): String {
        if (date == null) {
            return  ""
        }

        val curDate = Calendar.getInstance().time

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
        cal.time = date

        val calCur = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
        calCur.time = curDate

        if (cal[Calendar.YEAR] == calCur[Calendar.YEAR] && cal[Calendar.MONTH] == calCur[Calendar.MONTH] && cal[Calendar.DAY_OF_MONTH] == calCur[Calendar.DAY_OF_MONTH]) {
            return cal[Calendar.HOUR].toString().padStart(2, '0') + ":" + cal[Calendar.MINUTE].toString().padStart(2, '0')
        } else if (cal[Calendar.YEAR]  == calCur[Calendar.YEAR]) {
            return cal[Calendar.DAY_OF_MONTH].toString().padStart(2, '0') + "." + cal[Calendar.MONTH].toString().padStart(2, '0')
        }


        return cal[Calendar.DAY_OF_MONTH].toString().padStart(2, '0') + "." + cal[Calendar.MONTH].toString().padStart(2, '0') + "." + cal[Calendar.YEAR].toString().takeLast(2)
    }
}
