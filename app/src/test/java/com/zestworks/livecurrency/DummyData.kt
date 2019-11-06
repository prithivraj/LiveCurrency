package com.zestworks.livecurrency

import com.zestworks.helpers.NetworkResult
import com.zestworks.livecurrency.currencylist.CurrencyListResponse

val DUMMY_SUCCESS_RESPONSE = NetworkResult.Success(
    CurrencyListResponse(
        base = "EUR",
        date = "17-11-1991",
        rates = hashMapOf(
            "EUR" to 1.0,
            "INR" to 80.0
        )
    )
)

val DUMMY_SUCCESS_RESPONSE_RUPEE_GAINS = NetworkResult.Success(
    CurrencyListResponse(
        base = "EUR",
        date = "17-11-1991",
        rates = hashMapOf(
            "EUR" to 1.0,
            "INR" to 85.0
        )
    )
)

val DUMMY_FAILURE_RESPONSE = NetworkResult.Error(
    "OOPS."
)