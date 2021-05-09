package com.aggregator.api

val API = CompositeApi()

class CompositeApi {
    val api = mapOf("telegram" to TgApi(), "gmail" to GmailApi())
}