package com.spyrdonapps.currencyconverter.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.Currency
import com.spyrdonapps.currencyconverter.util.GlideApp
import com.spyrdonapps.currencyconverter.util.extensions.addTextChangedListener
import com.spyrdonapps.currencyconverter.util.extensions.capitalizeWords
import kotlinx.android.synthetic.main.item_currency.view.*
import timber.log.Timber
import java.lang.NumberFormatException
import java.util.Collections

class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private var currencies: MutableList<Currency> = mutableListOf()
    private var canUpdateList = true

    private val firstCurrency
        get() = currencies.first()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount() = currencies.count()

    override fun onAttachedToRecyclerView(recycler: RecyclerView) {
        super.onAttachedToRecyclerView(recycler)
        recyclerView = recycler
    }

    fun setData(updatedCurrencies: List<Currency>) {
        if (!canUpdateList || updatedCurrencies.isEmpty()) {
            return
        }
        if (currencies.isEmpty()) {
            currencies = Collections.synchronizedList(updatedCurrencies)
            notifyDataSetChanged()
        } else {
            updatedCurrencies.forEach { updatedCurrency ->
                currencies
                    .firstOrNull { it.isoCode == updatedCurrency.isoCode }
                    ?.let {
                        it.rateBasedOnEuro = updatedCurrency.rateBasedOnEuro
                        if (it.canChangeDisplayedRate) {
                            it.displayableValue = it.rateBasedOnEuro * firstCurrency.enteredValue
                        }
                    }
                currencies.filter { it.canChangeDisplayedRate }
                    .forEach {
                        notifyItemChanged(currencies.indexOf(it))
                    }
            }
        }
    }

    private fun moveItemToTopAndNotify(currency: Currency, position: Int) {
        currencies.apply {
            recyclerView.post {
                // todo check if this flag is still needed at the end
                canUpdateList = false
                try {
                    currencies.removeAt(position)
                    currencies.add(0, currency)
                    notifyItemMoved(position, 0)
                    scrollToTop()
                } catch (e: Exception) {
                    Timber.e(e)
                } finally {
                    canUpdateList = true
                }
            }
        }
    }

    private fun showKeyboard(view: View) {
        with(view) {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }
    }

    private fun scrollToTop() {
        (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 1)
    }

    private fun setCurrencyRateNotChangeable(currency: Currency) {
        currencies.forEach {
            it.canChangeDisplayedRate = it.isoCode != currency.isoCode
        }
    }

    private fun getCachedFormattedRateForCurrency(currency: Currency): String {
        return currencies.first { it.isoCode == currency.isoCode }.getFormattedDisplayableRateBasedOnEuro()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(currency: Currency) {
            with(view) {
                setOnClickListener {
                    onItemClicked(currency, adapterPosition, rateEditText)
                }

                isoCodeTextView.text = currency.isoCode
                fullNameTextView.text = currency.fullName.capitalizeWords()

                GlideApp.with(view)
                    .load(currency.flagImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(flagImageView)

                rateEditText.apply {
                    if (currency.canChangeDisplayedRate) {
                        setText(currency.getFormattedDisplayableRateBasedOnEuro())
                    } else {
                        setText(getCachedFormattedRateForCurrency(currency))
                    }
                    setOnTouchListener { _, _ ->
                        view.performClick()
                        true
                    }
                    addTextChangedListener { newText ->
                        currency.enteredValue = newText.toDoubleOrNull() ?: 0.toDouble()
                    }
                }
            }
        }

        private fun onItemClicked(currency: Currency, position: Int, rateEditText: EditText) {
            Timber.d("Currency clicked: ${currency.isoCode}")
            setCurrencyRateNotChangeable(currency)
            moveItemToTopAndNotify(currency, position)
            rateEditText.run {
                requestFocus()
                setSelection(text.length)
                showKeyboard(this)
            }
        }
    }

    companion object {
        const val EURO_ISO_CODE = "EUR"
    }
}