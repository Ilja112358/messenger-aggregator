package com.example.myapplication.ui.home

import DataHashGrpc
import Datahash
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.net.URL
import java.util.logging.Logger

class Api {
    private val logger = Logger.getLogger(this.javaClass.name)

    private fun channel(): ManagedChannel {
        val url = URL("http://84.252.137.106:6067")
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

    fun getHash(
        text: String
    ): String {
        val request = Datahash.Text.newBuilder().setData(text).build()
        val stub = DataHashGrpc.newBlockingStub(channel())
        val response = stub.hashSha256(request)

        return response.data
    }
}