package com.example.catchat

import com.example.myapplication.ui.home.Api

val API = CompositeApi()

class CompositeApi {
    public val api = mapOf<String, Api>("telegram" to TgApi(), "gmail" to GmailApi())
}