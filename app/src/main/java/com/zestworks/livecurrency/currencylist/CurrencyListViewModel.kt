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

    private var multiplier: Double = 1.0
    private var isRefreshLoopRunning = false
    lateinit var refreshLoopJob: Job

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
                val currentListCopy = value.data.items.toMutableList()
                val oldCurrency = currentListCopy[index]
                multiplier = newValue.div(oldCurrency.currencyValue).times(multiplier)
                currentListCopy.removeAt(index)
                currentListCopy.add(0, oldCurrency)
                _stateStream.postValue(
                    Content(
                        CurrencyListViewState(currentListCopy.map {
                            Currency(
                                it.currencyName,
                                it.currencyValue.times(multiplier)
                            )
                        })
                    )
                )
                refreshLoopJob = startRefreshLoop()
            }
        }
    }

    private fun startRefreshLoop(): Job {
        return viewModelScope.launch {
            while (true) {
                when (val latestRates = currencyListRepository.getLatestRates()) {
                    is NetworkResult.Success -> {
                        //Refreshing existing values
                        val currentState = _stateStream.value
                        if (currentState is Content) {
                            stateStream.postValue(
                                Content(
                                    CurrencyListViewState(
                                        currentState.data.items.map {
                                            Currency(
                                                it.currencyName,
                                                latestRates.data.rates[it.currencyName]!!.times(
                                                    multiplier
                                                )
                                            )
                                        }
                                    )
                                )

                            )
                        } else {
                            //Loading for the first time
                            _stateStream.postValue(
                                Content(
                                    CurrencyListViewState(
                                        latestRates.data.rates.map { Currency(it.key, it.value) }
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
