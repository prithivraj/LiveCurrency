package com.zestworks.livecurrency.currencylist

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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
        holder.currencyName.text = items[position].currencyName
        holder.currencyValue.apply {
            removeTextChangedListener(holder.textWatcher)
            //Because settext moves the cursor to the end.
            editableText.replace(0, editableText.length, items[position].currencyValue.toString())
            setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus){
                    currencyListAdapterCallbacks.onItemEdited(holder.adapterPosition, items[holder.adapterPosition].currencyValue)
                }
            }
            val watcher = doAfterTextChanged {
                val value = if(it.toString().isEmpty()){
                    0.0
                } else {
                    it.toString().toDouble()
                }
                currencyListAdapterCallbacks.onItemEdited(holder.adapterPosition, value)
            }
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
    val currencyName: TextView = row.findViewById(R.id.currency_name)
    val currencyValue: EditText = row.findViewById(R.id.currency_value)
    var textWatcher: TextWatcher? = null
}

interface CurrencyListAdapterCallbacks {
    fun onItemEdited(index: Int, newValue: Double)
}