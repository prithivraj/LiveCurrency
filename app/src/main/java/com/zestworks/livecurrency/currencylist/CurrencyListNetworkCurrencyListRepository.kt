package com.zestworks.livecurrency.currencylist

import com.zestworks.helpers.NetworkResult

class CurrencyListNetworkCurrencyListRepository(private val currencyListApi: CurrencyListApi) :
    CurrencyListRepository {
    override suspend fun getLatestRates(): NetworkResult<CurrencyListResponse> {
        val latestRates = currencyListApi.getLatestRates()
        return when (latestRates.isSuccessful) {
            true -> {
                val body = latestRates.body()
                return when (body == null) {
                    true -> {
                        NetworkResult.Error("Error : Empty body.")
                    }
                    false -> {
                        NetworkResult.Success(body)
                    }
                }
            }
            false -> {
                NetworkResult.Error("Error : Network request failed.")
            }
        }
    }
}