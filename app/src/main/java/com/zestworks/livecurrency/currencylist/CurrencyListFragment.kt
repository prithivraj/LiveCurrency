package com.zestworks.livecurrency.currencylist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.zestworks.helpers.LCEState
import com.zestworks.helpers.LCEState.*
import com.zestworks.livecurrency.R
import kotlinx.android.synthetic.main.currency_list_fragment.*

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
            when(it){
                Loading -> {
                    currency_loader.visibility = View.VISIBLE
                    currency_list.visibility = View.GONE
                    currency_error_message.visibility = View.GONE
                }
                is Content -> {
                    currency_loader.visibility = View.GONE
                    currency_error_message.visibility = View.GONE
                    currency_list.apply {
                        visibility = View.VISIBLE
                        if(layoutManager == null){
                            layoutManager = LinearLayoutManager(context)
                        }
                        if(adapter == null){
                            adapter = CurrencyListAdapter(it.data.items, object: CurrencyListAdapterCallbacks{
                                override fun onItemEdited(index: Int, newValue: Double) {
                                    viewModel.onItemEdited(index, newValue)
                                }
                            })
                        } else {
                            (adapter as CurrencyListAdapter).updateData(it.data.items)
                        }
                    }
                }
                is Error -> {
                    currency_loader.visibility = View.GONE
                    currency_list.visibility = View.GONE
                    currency_error_message.apply {
                        visibility = View.VISIBLE
                        text = it.errorMessage
                    }
                }
            }
        })

        viewModel.viewCreated()
    }
}
