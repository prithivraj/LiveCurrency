package com.zestworks.livecurrency.currencylist

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zestworks.helpers.countryFlagImages
import com.zestworks.helpers.getTextAsDouble
import com.zestworks.livecurrency.R

class CurrencyListAdapter(
    private var items: List<Currency>,
    private val currencyListAdapterCallbacks: CurrencyListAdapterCallbacks
) :
    RecyclerView.Adapter<CurrencyListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_list_fragment_item, parent, false)
        return CurrencyListViewHolder(itemView)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: CurrencyListViewHolder, position: Int) {
        val currentRowData = items[position]
        holder.currencyName.text = currentRowData.currencyName
        holder.currencyValue.apply {
            removeTextChangedListener(holder.textWatcher)
            if (getTextAsDouble() != currentRowData.currencyValue) {
                editableText.replace(0, editableText.length, currentRowData.currencyValue.toString())
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    currencyListAdapterCallbacks.onItemEdited(
                        holder.adapterPosition,
                        items[holder.adapterPosition].currencyValue
                    )
                }
            }
            val watcher = doAfterTextChanged {
                currencyListAdapterCallbacks.onItemEdited(holder.adapterPosition, getTextAsDouble())
            }
            Glide
                .with(context)
                .load(countryFlagImages[currentRowData.currencyName])
                .fitCenter()
                .into(holder.currencyImage)
            holder.textWatcher = watcher
        }
    }

    fun updateData(items: List<Currency>) {
        val oldItems = this.items
        val newItems = items
        this.items = newItems
        val diff = DiffUtil.calculateDiff(CurrencyListDiffCallback(oldItems, newItems), true)
        diff.dispatchUpdatesTo(this)
    }
}

class CurrencyListViewHolder(row: View) : RecyclerView.ViewHolder(row) {
    val currencyImage: ImageView = row.findViewById(R.id.currency_image)
    val currencyName: TextView = row.findViewById(R.id.currency_name)
    val currencyValue: EditText = row.findViewById(R.id.currency_value)
    var textWatcher: TextWatcher? = null
}

interface CurrencyListAdapterCallbacks {
    fun onItemEdited(index: Int, newValue: Double)
}