package com.zestworks.livecurrency.currencylist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zestworks.helpers.LCE
import com.zestworks.helpers.LCE.Content
import com.zestworks.helpers.LCE.Loading
import com.zestworks.helpers.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyListViewModel(private val currencyListRepository: CurrencyListRepository) :
    ViewModel() {

    //Single source of truth for the UI.
    private val _stateStream = MutableLiveData<LCE<CurrencyListViewState>>().apply {
        postValue(Loading)
    }
    val stateStream = _stateStream

    private var isRefreshLoopRunning = false
    private lateinit var refreshLoopJob: Job

    private var baseCurrency = Currency("EUR", 1.0)
    private var rates = hashMapOf<String, Double>()

    fun viewCreated() {
        if (!isRefreshLoopRunning) {
            isRefreshLoopRunning = true
            refreshLoopJob = startRefreshLoop()
        }
    }

    fun onItemEdited(index: Int, newValue: Double) {
        viewModelScope.launch {
            val value = _stateStream.value
            if (value is Content) {
                refreshLoopJob.cancel() // Prevent jumping cursors
                val currentStateCopy = value.data.items.toMutableList()
                val multiplier = newValue.div(value.data.items.first().currencyValue)
                currentStateCopy.removeAt(index)
                val newState = currentStateCopy.map {
                    Currency(
                        it.currencyName,
                        rates[it.currencyName]!!.times(multiplier)
                    )
                }
                val newBase = baseCurrency.copy(
                    currencyValue = newValue
                )
                _stateStream.postValue(
                    Content(
                        CurrencyListViewState(
                            mutableListOf<Currency>().plus(newBase).plus(newState)
                        )
                    )
                )
                refreshLoopJob = startRefreshLoop()
            }
        }
    }

    fun onItemFocused(index: Int) {
        viewModelScope.launch {
            val value = _stateStream.value
            if (value is Content) {
                refreshLoopJob.cancel() // Prevent jumping cursors
                val mutableCurrentState = value.data.items.toMutableList()
                val editedCurrency = mutableCurrentState[index]
                rates[baseCurrency.currencyName] = baseCurrency.currencyValue
                rates.remove(editedCurrency.currencyName)
                baseCurrency = editedCurrency.copy(
                    currencyName = editedCurrency.currencyName,
                    currencyValue = editedCurrency.currencyValue
                )
                mutableCurrentState.removeAt(index)
                mutableCurrentState.add(0, baseCurrency)
                _stateStream.postValue(value.copy(data = CurrencyListViewState(mutableCurrentState)))
                refreshLoopJob = startRefreshLoop()
            }
        }
    }

    private fun startRefreshLoop(): Job {
        return viewModelScope.launch {
            while (true) {
                delay(1000)
                when (val latestRates =
                    currencyListRepository.getLatestRates(baseCurrency.currencyName)) {
                    is NetworkResult.Success -> {
                        //Refreshing existing values
                        val currentState = _stateStream.value
                        if (currentState is Content) {
                            val baseCurrencyUserValue =
                                currentState.data.items.first().currencyValue
                            val newConversionMap = HashMap(latestRates.data.rates)
                            newConversionMap.entries.forEach {
                                it.setValue(it.value.times(baseCurrencyUserValue))
                            }
                            rates = newConversionMap
                            val newState = mutableListOf<Currency>()
                                .plus(
                                    currentState.data.items.map {
                                        if (it == currentState.data.items.first()) {
                                            currentState.data.items.first()
                                        } else {
                                            Currency(
                                                it.currencyName,
                                                rates[it.currencyName]!!
                                            )
                                        }
                                    }
                                )
                            stateStream.postValue(
                                Content(
                                    CurrencyListViewState(
                                        newState
                                    )
                                )

                            )
                        } else {
                            //Loading for the first time
                            rates = HashMap(latestRates.data.rates)
                            val newState = mutableListOf<Currency>()
                                .plus(baseCurrency)
                                .plus(latestRates.data.rates.map {
                                    Currency(it.key, it.value.times(baseCurrency.currencyValue))
                                })
                            _stateStream.postValue(
                                Content(
                                    CurrencyListViewState(
                                        newState
                                    )
                                )
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _stateStream.postValue(LCE.Error(latestRates.errorMessage))
                    }
                }
            }
        }
    }
}
