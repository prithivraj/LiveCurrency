package com.zestworks.livecurrency.currencylist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.zestworks.livecurrency.R

class CurrencyListFragment : Fragment() {

    private lateinit var viewModel: CurrencyListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.currency_list_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(
            activity!!,
            CurrencyListViewModelFactory()
        )[CurrencyListViewModel::class.java]

        viewModel.stateStream.observe(activity!!, Observer {
            Log.d("CurrencyListFragment", it.toString())
        })

        viewModel.viewCreated()
    }
}
