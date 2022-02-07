package no.nav.fo.veilarbregistrering.http

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun defaultHttpClient(): OkHttpClient =
    defaultHttpClientBuilder()
        .build()

fun buildHttpClient(block: OkHttpClient.Builder.() -> Unit): OkHttpClient =
    defaultHttpClientBuilder().apply(block).build()

private fun defaultHttpClientBuilder() =
    OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(LogInterceptor())
        .connectTimeout(10, TimeUnit.SECONDS)
        .followRedirects(false)
