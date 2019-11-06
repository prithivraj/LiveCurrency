package com.zestworks.livecurrency

import com.zestworks.helpers.NetworkResult
import com.zestworks.livecurrency.currencylist.CurrencyListResponse

val DUMMY_SUCCESS_RESPONSE = NetworkResult.Success(
    CurrencyListResponse(
        base = "EUR",
        date = "17-11-1991",
        rates = hashMapOf(
            "EUR" to 1.0,
            "AUD" to 1.6192
        )
    )
)

val DUMMY_FAILURE_RESPONSE = NetworkResult.Error(
    "OOPS."
)