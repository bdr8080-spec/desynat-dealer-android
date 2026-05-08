package com.desynatdealer.controller

import android.content.Context
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesCommonApiService(okHttpClient: OkHttpClient): CommonApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
          //  .client(OkHttpClientHelper().getOkHttpClient())
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CommonApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(ConditionalLoggingInterceptor())
          //  .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
           /* .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

            //    val token = SharedPreference.get(context).accessToken
                val token = if (SharedPreference.get(context).userType=="1") SharedPreference.get(context).staffToken else SharedPreference.get(context).accessToken

                if (token.isNotEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }*/
            .addInterceptor { chain ->

                val originalRequest = chain.request()

                val existingAuth = originalRequest.header("Authorization")

                if (!existingAuth.isNullOrEmpty()) {
                    return@addInterceptor chain.proceed(originalRequest)
                }

                val requestBuilder = originalRequest.newBuilder()

                val token = SharedPreference.get(context).accessToken

                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                    requestBuilder.addHeader("Accept-Language", Constants.language)
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

}