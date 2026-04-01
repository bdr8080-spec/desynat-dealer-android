package com.desynatdealer.controller

import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class ConditionalLoggingInterceptor : Interceptor {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val isMultipartUpload = request.body is MultipartBody

        return if (isMultipartUpload) {
            chain.proceed(request)
        } else {
            loggingInterceptor.intercept(chain)
        }
    // return loggingInterceptor.intercept(chain)
    }
}