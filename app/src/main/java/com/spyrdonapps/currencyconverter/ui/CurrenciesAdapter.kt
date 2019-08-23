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
import kotlinx.android.synthetic.main.item_currency.view.*
import timber.log.Timber
import java.util.Collections

class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private var curriencies: MutableList<Currency> = mutableListOf()
    private var canUpdateList = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(curriencies[position])
    }

    override fun getItemCount() = curriencies.count()

    override fun onAttachedToRecyclerView(recycler: RecyclerView) {
        super.onAttachedToRecyclerView(recycler)
        recyclerView = recycler
    }

    fun setData(newCurrencies: List<Currency>) {
        if (!canUpdateList || newCurrencies.isEmpty()) {
            return
        }
        if (curriencies.isEmpty()) {
            curriencies = Collections.synchronizedList(newCurrencies)
        } else {
            newCurrencies.forEach { updatedCurrency ->
                curriencies.firstOrNull { it.isoCode == updatedCurrency.isoCode }?.let {
                    it.rateBasedOnEuro = updatedCurrency.rateBasedOnEuro
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun moveItemToTopAndNotify(currency: Currency, position: Int) {
        curriencies.apply {
            recyclerView.post {
                canUpdateList = false
                try {
                    // TODO instead of sorting: move second item directly to it's place and it's basically done
//                    subList(1, lastIndex).sortBy { it.isoCode } // this won't work

                    curriencies.removeAt(position)
                    curriencies.add(0, currency)
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

    private fun setCurrencyRateNotChangable(currency: Currency) {
        curriencies.forEach {
            it.canChangeRate = it.isoCode != currency.isoCode
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(currency: Currency) {
            with(view) {
                setOnClickListener {
                    onItemClicked(currency, adapterPosition, rateEditText)
                }

                isoCodeTextView.text = currency.isoCode
                fullNameTextView.text = currency.fullName

                GlideApp.with(view)
                    .load(currency.flagImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(flagImageView)

                rateEditText.apply {
                    // TODO this doesn't work very good, because viewholder needs to delete old rate value from previous item
                    // I need to handle clearing this view in some way before I put the values here, I may cache the snapshot of
                    // all currencies while item was clicked and let this currency stay at it + freeze it until user is done
                    // typing. need additional checks
                    if (currency.canChangeRate) {
                        setText(currency.rateBasedOnEuro.toString())
                    }
                    setOnTouchListener { _, _ ->
                        view.performClick()
                        true
                    }
                }
            }
        }

        private fun onItemClicked(currency: Currency, position: Int, rateEditText: EditText) {
            Timber.d("Currency clicked: ${currency.isoCode}")
            setCurrencyRateNotChangable(currency)
            moveItemToTopAndNotify(currency, position)
            rateEditText.run {
                requestFocus()
                showKeyboard(this)
            }
        }
    }

    companion object {
        const val EURO_ISO_CODE = "EUR"
    }
}