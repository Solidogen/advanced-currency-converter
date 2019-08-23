package com.spyrdonapps.currencyconverter.util.extensions

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }