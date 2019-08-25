package com.spyrdonapps.currencyconverter.test.ui


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule

import org.junit.Assert.*
import com.spyrdonapps.currencyconverter.ui.MainActivity
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
class CurrenciesTest {

    @get:Rule
    var activityActivityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun useAppContext() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("com.spyrdonapps.currencyconverter", appContext.packageName)
    }

    @Test
    fun isActionBarDisplayed() {
        onView(withText("Rates")).check(matches(isDisplayed()))
    }

    // TODO idling resource

    // tests: wait for data, click second view, first view has it's content
    // tests: wait for data, click second view, first view has it's content

}
