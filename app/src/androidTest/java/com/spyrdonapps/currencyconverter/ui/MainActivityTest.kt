package com.spyrdonapps.currencyconverter.ui


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.test.util.withViewAtPosition
import kotlinx.android.synthetic.main.item_currency.view.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random
import kotlin.random.nextInt


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    // region constants

    // endregion constants

    // region helper fields

    @get:Rule
    var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    // endregion helper fields

    @Before
    fun setup() {
        Thread.sleep(3000)
    }

    @Test
    fun mainActivity_dataIsLoaded_recyclerHasItems() {
        for (position in 1..5) {
            onRecycler().check(matches(withViewAtPosition(position, allOf(isDisplayed(), hasDescendant(withId(R.id.isoCodeTextView))))))
        }
    }

    @Test
    fun mainActivity_dataIsLoaded_firstCurrencyIsEuro() {
        onRecycler().check(matches(withViewAtPosition(0, allOf(isDisplayed(), hasDescendant(withText(CurrenciesAdapter.EURO_ISO_CODE))))))
    }

    @Test
    fun mainActivity_dataIsLoadedAndRandomItemsAreClicked_itemsSwapPlaces() {
        val recyclerView = activityTestRule.activity.findViewById<RecyclerView>(R.id.recyclerView)
        repeat(10) {
            val position = Random.Default.nextInt(1, 6)
            val clickedViewCurrencyIsoName = (recyclerView.findViewHolderForAdapterPosition(position) as? CurrenciesAdapter.ViewHolder)?.itemView?.isoCodeTextView?.text.toString()

            onRecycler().perform(RecyclerViewActions.actionOnItemAtPosition<CurrenciesAdapter.ViewHolder>(position, click()))
            onRecycler().check(matches(withViewAtPosition(0, allOf(isDisplayed(), hasDescendant(withText(clickedViewCurrencyIsoName))))))
        }
    }

    // region helper methods

    private fun onRecycler() = onView(withId(R.id.recyclerView)).inRoot(withDecorView(`is`(activityTestRule.activity.window.decorView)))

    // endregion helper methods

    // region helper classes

    // endregion helper classes

}
