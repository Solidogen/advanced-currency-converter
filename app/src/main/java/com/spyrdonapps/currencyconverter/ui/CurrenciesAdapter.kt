package com.spyrdonapps.currencyconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spyrdonapps.currencyconverter.R
import com.spyrdonapps.currencyconverter.data.model.CurrencyUiModel
import com.spyrdonapps.currencyconverter.util.GlideApp
import kotlinx.android.synthetic.main.item_currency.view.*
import timber.log.Timber
import java.lang.Exception
import java.util.Collections

class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private var currentTopCurrencyIsoCode: String = EURO_ISO_CODE

    private var items: MutableList<CurrencyUiModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_currency,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    override fun onAttachedToRecyclerView(recycler: RecyclerView) {
        super.onAttachedToRecyclerView(recycler)
        recyclerView = recycler
    }

    fun setData(list: List<CurrencyUiModel>) {
        items = Collections.synchronizedList(list)
        with(items) {
            val previousTopItem = first { it.isoCode == currentTopCurrencyIsoCode }
            remove(previousTopItem)
            add(0, previousTopItem)
            notifyDataSetChanged()
        }
    }

    private fun moveItemToTopAndNotify(item: CurrencyUiModel) {
        items.apply {
            val selectedItemIndex = indexOf(item)
            Collections.swap(this, selectedItemIndex, 0)
            notifyItemMoved(selectedItemIndex, 0)
            // sort items besides first one
            subList(1, lastIndex).sortBy { it.isoCode }
        }
    }

    private fun scrollToTop() {
        (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 1)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: CurrencyUiModel) {
            with(view) {
                isoCodeTextView.text = item.isoCode
                fullNameTextView.text
                rateEditText.setText(item.rateBasedOnEuro.toString())
                setOnClickListener {
                    onItemClicked(item)
                }
                GlideApp.with(view)
                    .load(item.flagImageUrl)
                    .circleCrop()
                    .into(flagImageView)
                rateEditText.setOnTouchListener { _, _ ->
                    view.performClick()
                    false
                }
            }
        }

        private fun onItemClicked(item: CurrencyUiModel) {
            try {
                currentTopCurrencyIsoCode = item.isoCode
                moveItemToTopAndNotify(item)
                scrollToTop()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    companion object {
        const val EURO_ISO_CODE = "EUR"
    }
}