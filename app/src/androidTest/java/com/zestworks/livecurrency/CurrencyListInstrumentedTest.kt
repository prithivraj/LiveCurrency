package com.zestworks.livecurrency

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.zestworks.livecurrency.RecyclerViewItemCountAssertion.withItemCount
import com.zestworks.livecurrency.currencylist.CurrencyListApi.Companion.okHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CurrencyListInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private val idlingResource = OkHttp3IdlingResource.create("okhttp", okHttpClient)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun checkIfListLoads() {
        onView(withId(R.id.currency_list)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfListContainsAllItems() {
        onView(withId(R.id.currency_list)).check(withItemCount(33))
    }

    @Test
    fun checkIfFirstItemIsEuro() {
        onView(withText("EUR")).check(matches(isDisplayed()))
    }

}
