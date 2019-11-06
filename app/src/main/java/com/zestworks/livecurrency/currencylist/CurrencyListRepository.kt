package com.zestworks.livecurrency.currencylist

import com.zestworks.helpers.NetworkResult

interface CurrencyListRepository {
    suspend fun getLatestRates(): NetworkResult<CurrencyListResponse>
}