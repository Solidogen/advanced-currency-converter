package com.spyrdonapps.currencyconverter.ui


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.spyrdonapps.currencyconverter.R
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import android.view.View
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher


@RunWith(AndroidJUnit4::class)
class CurrenciesTest {

    // TODO make at least 2 reasonable tests

    @get:Rule
    var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setup() {
        Thread.sleep(3000)
    }

    // https://github.com/WasimMemon/Myapplications/blob/master/Tutorials/app/src/androidTest/java/com/androprogrammer/tutorials/ListActivityUITestCases.java?source=post_page-----eb2ceaddc74f----------------------

    @Test
    fun mainActivity_dataIsLoaded_euroCurrencyIsVisible() {
        onView(withText("EUR")).check(matches(isDisplayed()))
    }

//     tests: wait for data, click second view, first view has it's content
    @Test
    fun mainActivity_dataIsLoaded_recyclerHasItems() {
        onRecycler().check(matches(withViewAtPosition(0, allOf(withId(R.id.isoCodeTextView), isDisplayed()))))
    }

    @Test
    fun mainActivity_dataIsLoaded_firstCurrencyIsEuro() {
        onRecycler().check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    private fun onRecycler() = onView(withId(R.id.recyclerView)).inRoot(withDecorView(`is`(activityTestRule.activity.window.decorView)))

    private fun withViewAtPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}
