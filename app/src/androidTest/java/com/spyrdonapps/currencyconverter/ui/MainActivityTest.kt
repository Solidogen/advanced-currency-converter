package com.spyrdonapps.currencyconverter.ui


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.test.util.withViewAtPosition
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setup() {
        Thread.sleep(1000)
    }

    @Test
    fun mainActivity_dataIsLoaded_recyclerHasItems() {
        Thread.sleep(3000) // extra sleep first time, later items show almost instantly
        (1..5).forEach { position ->
            onRecycler().check(matches(withViewAtPosition(position, allOf(isDisplayed(), hasDescendant(withId(R.id.isoCodeTextView))))))
        }
    }

    @Test
    fun mainActivity_dataIsLoaded_firstCurrencyIsEuro() {
        onRecycler().check(matches(withViewAtPosition(0, allOf(isDisplayed(), hasDescendant(withText(CurrenciesAdapter.EURO_ISO_CODE))))))
    }

    //     tests: wait for data, click second view, first view has it's content

    private fun onRecycler() = onView(withId(R.id.recyclerView)).inRoot(withDecorView(`is`(activityTestRule.activity.window.decorView)))
}
