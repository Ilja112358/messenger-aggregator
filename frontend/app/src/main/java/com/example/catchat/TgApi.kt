package com.example.catchat


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.models.Dialog
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.net.URL
import java.util.logging.Logger
import java.util.stream.Collectors

class TgApi {
    private val logger = Logger.getLogger(this.javaClass.name)

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
        val stub = TgApiGrpc.newBlockingStub(channel())
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
        val stub = TgApiGrpc.newBlockingStub(channel())
        val response = stub.auth(request)
    }

    fun getDialogs(
        uid: String
    ) : List<Dialog> {
        val request = Tg.User.newBuilder().setUid(uid).build()
        val stub = TgApiGrpc.newBlockingStub(channel())
        val response = stub.getDialogs(request)
        return response.dialogList.stream().map { d -> Dialog(d.name, d.message, d.date) }.collect(Collectors.toList())
    }
}
