package com.spyrdonapps.currencyconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.CurrencyUiModel
import kotlinx.android.synthetic.main.item_currency.view.*
import java.util.Collections

class CurrenciesAdapter : ListAdapter<CurrencyUiModel, CurrenciesAdapter.ViewHolder>(DiffCallback) {

    // TODO zawsze updatuja sie ratesy wszystkie poza tym na gorze (domyslnie euro, przy dotyku itemu ten item leci do gory i stoi)

    private var canUpdateList = true
    private lateinit var movingItemsHelperList: MutableList<CurrencyUiModel>

    private lateinit var recyclerView: RecyclerView
    private var currentTopCurrencyIsoCode: String = EURO_ISO_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onAttachedToRecyclerView(recycler: RecyclerView) {
        super.onAttachedToRecyclerView(recycler)
        recyclerView = recycler
    }

    fun setData(list: List<CurrencyUiModel>) {
        if (!canUpdateList) {
            return
        }
        movingItemsHelperList = Collections.synchronizedList(list.toMutableList())
        with(movingItemsHelperList) {
            val previousTopItem = first { it.isoCode == currentTopCurrencyIsoCode }
            remove(previousTopItem)
            add(0, previousTopItem)
            submitList(movingItemsHelperList)
        }
    }

    private fun scrollToTop() {
        recyclerView.layoutManager?.startSmoothScroll(object : LinearSmoothScroller(recyclerView.context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }.apply {
            targetPosition = 0
        })
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: CurrencyUiModel) {
            with(view) {
                isoCodeTextView.text = item.isoCode
                rateTextView.text = item.rateBasedOnEuro.toString()
                setOnClickListener {
                    onItemClicked(item)
                }
            }
        }

        private fun onItemClicked(item: CurrencyUiModel) {
            canUpdateList = false
            currentTopCurrencyIsoCode = item.isoCode
            movingItemsHelperList.apply {
                sortBy { it.isoCode }
                remove(item)
                add(0, item)
            }
            notifyDataSetChanged()
            scrollToTop()
            canUpdateList = true
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<CurrencyUiModel>() {

        override fun areItemsTheSame(oldItem: CurrencyUiModel, newItem: CurrencyUiModel): Boolean {
            return oldItem.isoCode == newItem.isoCode
        }

        override fun areContentsTheSame(oldItem: CurrencyUiModel, newItem: CurrencyUiModel): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val EURO_ISO_CODE = "EUR"
    }
}