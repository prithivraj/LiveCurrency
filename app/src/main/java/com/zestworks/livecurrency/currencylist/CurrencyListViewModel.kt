package com.zestworks.livecurrency.currencylist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zestworks.helpers.LCEState
import com.zestworks.helpers.LCEState.Content
import com.zestworks.helpers.LCEState.Loading
import com.zestworks.helpers.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyListViewModel(private val currencyListRepository: CurrencyListRepository) :
    ViewModel() {

    private val _stateStream = MutableLiveData<LCEState<CurrencyListViewState>>().apply {
        postValue(Loading)
    }
    val stateStream = _stateStream

    private var isRefreshLoopRunning = false
    private lateinit var refreshLoopJob: Job
    private var baseCurrency = Currency("EUR", 1.0)

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
                val editedCurrency = currentStateCopy[index]
                baseCurrency = editedCurrency.copy(
                    currencyValue = newValue
                )
                val multiplier = newValue.div(editedCurrency.currencyValue)
                currentStateCopy.removeAt(index)
                val map = currentStateCopy.map {
                    Currency(
                        it.currencyName,
                        it.currencyValue.times(multiplier)
                    )
                }
                val newState = mutableListOf<Currency>()
                    .plus(baseCurrency)
                    .plus(map)
                _stateStream.postValue(
                    Content(
                        CurrencyListViewState(
                            newState
                        )
                    )
                )
                refreshLoopJob = startRefreshLoop()
            }
        }
    }

    private fun startRefreshLoop(): Job {
        return viewModelScope.launch {
            while (true) {
                when (val latestRates =
                    currencyListRepository.getLatestRates(baseCurrency.currencyName)) {
                    is NetworkResult.Success -> {
                        //Refreshing existing values
                        val currentState = _stateStream.value
                        if (currentState is Content) {
                            val newState = mutableListOf<Currency>()
                                .plus(
                                    currentState.data.items.map {
                                        if(baseCurrency.currencyName == it.currencyName){
                                            baseCurrency
                                        } else {
                                            Currency(
                                                it.currencyName,
                                                latestRates.data.rates[it.currencyName]!!.times(baseCurrency.currencyValue)
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
                        _stateStream.postValue(LCEState.Error(latestRates.errorMessage))
                    }
                }
                delay(1000)
            }
        }
    }
}
