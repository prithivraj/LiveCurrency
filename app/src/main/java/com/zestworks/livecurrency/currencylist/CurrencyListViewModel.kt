package com.zestworks.livecurrency.currencylist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zestworks.helpers.LCEState
import com.zestworks.helpers.LCEState.Loading
import com.zestworks.helpers.NetworkResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyListViewModel(private val currencyListRepository: CurrencyListRepository) : ViewModel() {

    private val _stateStream = MutableLiveData<LCEState<CurrencyListViewState>>().apply {
        postValue(Loading)
    }
    val stateStream = _stateStream

    fun viewCreated(){
        startRefreshLoop()
    }

    private fun startRefreshLoop() {
        viewModelScope.launch {
            while (true) {
                when (val latestRates = currencyListRepository.getLatestRates()) {
                    is NetworkResult.Success -> {
                        _stateStream.postValue(LCEState.Content(
                            CurrencyListViewState(
                                latestRates.data.rates
                            )
                        ))
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
