package com.spyrdonapps.currencyconverter.test.util

import org.mockito.Mockito.mock

inline fun <reified T> mock(): T = mock(T::class.java)