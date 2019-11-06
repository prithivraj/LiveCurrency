package com.zestworks.livecurrency.currencylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CurrencyListViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyListViewModel::class.java)) {
            return CurrencyListViewModel(
                CurrencyListNetworkCurrencyListRepository(
                    CurrencyListApi.create()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}