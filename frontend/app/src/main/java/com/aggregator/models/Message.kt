package com.aggregator.models

data class Message(
    val userName: String,
    val text: String,
    val timestamp: String,
    val isUserMessage: Boolean,
    val unixTs: Long,
    val attachementType: String,
    val attachementUrl: String)