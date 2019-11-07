package com.zestworks.livecurrency.currencylist

import androidx.recyclerview.widget.DiffUtil

class CurrencyListDiffCallback(private val oldList: List<Currency>, private val newList: List<Currency>) :
    DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].currencyName == newList[newItemPosition].currencyName
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].currencyValue == newList[newItemPosition].currencyValue
    }
}