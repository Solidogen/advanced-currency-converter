package com.spyrdonapps.currencyconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.CurrencyUiModel
import kotlinx.android.synthetic.main.item_currency.view.*

class CurrenciesAdapter : ListAdapter<CurrencyUiModel, CurrenciesAdapter.ViewHolder>(DiffCallback) {

    // TODO zawsze updatuja sie ratesy wszystkie poza tym na gorze (domyslnie euro, przy dotyku itemu ten item leci do gory i stoi)

    private var currentTopCurrencyIsoCode: String = EURO_ISO_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setData(list: List<CurrencyUiModel>) {
        with(list.toMutableList()) {
            val topItem = first { it.isoCode == currentTopCurrencyIsoCode }
            removeAt(indexOf(topItem))
            add(0, topItem)
            submitList(this)
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: CurrencyUiModel) {
            with(view) {
                isoCodeTextView.text = item.isoCode
                rateTextView.text = item.rateBasedOnEuro.toString()
                setOnClickListener {
                    currentTopCurrencyIsoCode = item.isoCode
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<CurrencyUiModel>() {

        override fun areItemsTheSame(oldItem: CurrencyUiModel, newItem: CurrencyUiModel): Boolean {
            return oldItem.isoCode == newItem.isoCode
        }

        override fun areContentsTheSame(oldItem: CurrencyUiModel, newItem: CurrencyUiModel): Boolean {
            return oldItem.rateBasedOnEuro == newItem.rateBasedOnEuro
        }
    }

    companion object {
        const val EURO_ISO_CODE = "EUR"
    }
}