package com.zestworks.livecurrency.currencylist

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CurrencyListApi {

    @GET("latest?base=EUR")
    suspend fun getLatestRates(): Response<CurrencyListResponse>

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

        private val okHttpClient: OkHttpClient by lazy {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("CurrencyListApi", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
        }
    }
}