package com.test.audioandaccelerometerrecord.rest

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class BaseHttp(val dir : File) {
    var httpClient: OkHttpClient? = null

    private val logginInterceptor: HttpLoggingInterceptor
        get() {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return loggingInterceptor
        }

    init {
        if (httpClient == null)
            httpClient = createHttpClient()
    }

    private fun createHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient().newBuilder()
        httpClientBuilder.connectTimeout(60L, TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(60L, TimeUnit.SECONDS)
        httpClientBuilder.addInterceptor(logginInterceptor)
        httpClientBuilder.cache(createCache())

        return httpClientBuilder.build()
    }

    private fun createCache(): Cache {
        val size = 10 * 1024 * 1024//10mb
        return Cache(dir, size.toLong())
    }

}