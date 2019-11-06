package com.zestworks.livecurrency.currencylist
import com.google.gson.annotations.SerializedName

data class CurrencyListResponse(
    @SerializedName("base")
    val base: String, // EUR
    @SerializedName("date")
    val date: String, // 2018-09-06
    @SerializedName("rates")
    val rates: HashMap<String, Double>
)