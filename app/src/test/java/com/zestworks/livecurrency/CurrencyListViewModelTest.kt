package com.zestworks.livecurrency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import com.zestworks.helpers.LCE.Content
import com.zestworks.helpers.LCE.Error
import com.zestworks.helpers.LCE.Loading
import com.zestworks.livecurrency.currencylist.Currency
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

    @Test
    fun `A loader is shown initially`() {
        viewModel.stateStream.value shouldBe Loading
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `There is one call every second to update currencies`() {
        every {
            runBlocking {
                mockRepository.getLatestRates("EUR")
            }
        } returns DUMMY_SUCCESS_RESPONSE

        viewModel.viewCreated()
        testDispatcher.advanceTimeBy(3000)

        verify(exactly = 3) {
            runBlocking { mockRepository.getLatestRates("EUR") }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `A successful network call updates UI correctly`() {
        every {
            runBlocking {
                mockRepository.getLatestRates("EUR")
            }
        } returns DUMMY_SUCCESS_RESPONSE

        viewModel.viewCreated()
        testDispatcher.advanceTimeBy(1000)

        val stateAfterCreation = viewModel.stateStream.value
        val responseList = DUMMY_SUCCESS_RESPONSE.data.rates.map { Currency(it.key, it.value) }
        stateAfterCreation shouldBe Content(
            CurrencyListViewState(
                mutableListOf<Currency>()
                    .plus(Currency("EUR", 1.0))
                    .plus(responseList)
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `A network call that fails updates the UI correctly`() {
        every {
            runBlocking {
                mockRepository.getLatestRates("EUR")
            }
        } returns DUMMY_FAILURE_RESPONSE
        viewModel.viewCreated()
        testDispatcher.advanceTimeBy(1000)
        viewModel.stateStream.value shouldBe Error(DUMMY_FAILURE_RESPONSE.errorMessage)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Config changes works correctly`() {
        every {
            runBlocking {
                mockRepository.getLatestRates("EUR")
            }
        } returns DUMMY_SUCCESS_RESPONSE
        viewModel.viewCreated()
        viewModel.viewCreated()
        viewModel.viewCreated()
        testDispatcher.advanceTimeBy(3000)
        //4 times because it is called once at the 0th second.
        verify(exactly = 3) {
            runBlocking { mockRepository.getLatestRates("EUR") }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Editing an item sends it to the top of the list`() {
        every {
            runBlocking {
                mockRepository.getLatestRates("EUR")
            }
        } returns DUMMY_SUCCESS_RESPONSE

        every {
            runBlocking {
                mockRepository.getLatestRates("INR")
            }
        } returns DUMMY_SUCCESS_RESPONSE_INR

        viewModel.viewCreated()
        testDispatcher.advanceTimeBy(1000)

        var currentState = viewModel.stateStream.value as Content
        currentState.data.items[0].currencyName shouldBe "EUR"
        viewModel.onItemFocused(1)
        viewModel.onItemEdited(0, 160.0)
        currentState = viewModel.stateStream.value as Content
        currentState.data.items[0].currencyName shouldBe "INR"
        currentState.data.items[0].currencyValue shouldBe 160.0
        currentState.data.items[1].currencyName shouldBe "EUR"
        currentState.data.items[1].currencyValue shouldBe 2.0

        // Once when ViewModel was initiated, once after the conversion edit was performed
        verify(exactly = 1) {
            runBlocking { mockRepository.getLatestRates("EUR") }
        }
        testDispatcher.advanceTimeBy(1000)
        verify(exactly = 1) {
            runBlocking { mockRepository.getLatestRates("INR") }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Subsequent updates to rates are rendered correctly`() {
        every {
            runBlocking {
                mockRepository.getLatestRates("EUR")
            }
        } returns DUMMY_SUCCESS_RESPONSE
        viewModel.viewCreated()
        testDispatcher.advanceTimeBy(1000)
        var currentState = viewModel.stateStream.value as Content
        currentState.data.items[0].currencyName shouldBe "EUR"
        viewModel.onItemEdited(0, 3.0)
        every {
            runBlocking {
                mockRepository.getLatestRates("EUR")
            }
        } returns DUMMY_SUCCESS_RESPONSE_RUPEE_GAINS

        testDispatcher.advanceTimeBy(2000)
        currentState = viewModel.stateStream.value as Content
        currentState.data.items[0].currencyName shouldBe "EUR"
        currentState.data.items[0].currencyValue shouldBe 3.0
        currentState.data.items[1].currencyName shouldBe "INR"
        currentState.data.items[1].currencyValue shouldBe 255.0
    }
}