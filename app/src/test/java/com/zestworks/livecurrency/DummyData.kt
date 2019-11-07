package com.zestworks.livecurrency

import com.zestworks.helpers.NetworkResult
import com.zestworks.livecurrency.currencylist.CurrencyListResponse

val DUMMY_SUCCESS_RESPONSE = NetworkResult.Success(
    CurrencyListResponse(
        base = "EUR",
        date = "17-11-1991",
        rates = hashMapOf(
            "INR" to 80.0,
            "THB" to 160.0
        )
    )
)

val DUMMY_SUCCESS_RESPONSE_INR = NetworkResult.Success(
    CurrencyListResponse(
        base = "INR",
        date = "17-11-1991",
        rates = hashMapOf(
            "EUR" to 0.0125,
            "THB" to 0.0250
        )
    )
)

val DUMMY_SUCCESS_RESPONSE_RUPEE_GAINS = NetworkResult.Success(
    CurrencyListResponse(
        base = "EUR",
        date = "17-11-1991",
        rates = hashMapOf(
            "INR" to 85.0,
            "THB" to 160.0
        )
    )
)

val DUMMY_FAILURE_RESPONSE = NetworkResult.Error(
    "OOPS."
)