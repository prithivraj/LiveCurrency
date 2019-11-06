package com.zestworks.livecurrency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import com.zestworks.helpers.LCEState
import com.zestworks.livecurrency.currencylist.CurrencyListRepository
import com.zestworks.livecurrency.currencylist.CurrencyListViewModel
import com.zestworks.livecurrency.currencylist.CurrencyListViewState
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CurrencyListViewModelTest {

    private val mockRepository: CurrencyListRepository = mockk()
    private lateinit var viewModel: CurrencyListViewModel

    @ExperimentalCoroutinesApi
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CurrencyListViewModel(mockRepository)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `There is one network call every second to update currencies`() {
        every {
            runBlocking {
                mockRepository.getLatestRates()
            }
        } returns DUMMY_SUCCESS_RESPONSE
        viewModel.viewCreated()
        testDispatcher.advanceTimeBy(3000)
        //4 times because it is called once at the 0th second.
        verify(exactly = 4) {
            runBlocking { mockRepository.getLatestRates() }
        }
    }

    @Test
    fun `A successful network call updates UI correctly`() {
        viewModel.stateStream.value shouldBe LCEState.Loading
        every {
            runBlocking {
                mockRepository.getLatestRates()
            }
        } returns DUMMY_SUCCESS_RESPONSE
        viewModel.viewCreated()
        viewModel.stateStream.value shouldBe LCEState.Content(CurrencyListViewState(DUMMY_SUCCESS_RESPONSE.data.rates))
    }

    @Test
    fun `A network call that fails updates the UI correctly`(){
        viewModel.stateStream.value shouldBe LCEState.Loading
        every {
            runBlocking {
                mockRepository.getLatestRates()
            }
        } returns DUMMY_FAILURE_RESPONSE
        viewModel.viewCreated()
        viewModel.stateStream.value shouldBe LCEState.Error(DUMMY_FAILURE_RESPONSE.errorMessage)
    }
}