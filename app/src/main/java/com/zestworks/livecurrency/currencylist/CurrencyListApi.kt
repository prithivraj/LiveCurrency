package com.zestworks.livecurrency.currencylist

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyListApi {

    @GET("latest")
    suspend fun getLatestRates(@Query("base") base: String): Response<CurrencyListResponse>

    companion object {
        private const val BASE_URL = "https://revolut.duckdns.org/"
        fun create(): CurrencyListApi {
            val gson = GsonBuilder().create()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(CurrencyListApi::class.java)
        }

        private val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.d("CurrencyListApi", it)
        }).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }
}