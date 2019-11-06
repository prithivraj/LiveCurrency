package com.zestworks.livecurrency.currencylist

data class CurrencyListViewState(
    val items: List<Currency>
)

data class Currency(
    val currencyName: String,
    val currencyValue: Double
)