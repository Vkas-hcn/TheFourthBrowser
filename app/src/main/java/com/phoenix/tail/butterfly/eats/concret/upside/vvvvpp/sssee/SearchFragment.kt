package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.sssee

import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentSearchBinding
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.searchData
import java.util.UUID

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(){
    override val layoutId: Int
        get() = R.layout.fragment_search

    override val viewModelClass: Class<SearchViewModel>
        get() = SearchViewModel::class.java


    override fun setupViews() {
        binding.imgBack.setOnClickListener {
            customizeReturnKey()
        }
        binding.llBing.setOnClickListener {
            checkReturnKey("1")
        }
        binding.llGoogle.setOnClickListener {
            checkReturnKey("2")
        }
        binding.llYahoo.setOnClickListener {
            checkReturnKey("3")
        }
        binding.llDuck.setOnClickListener {
            checkReturnKey("4")
        }
    }

    private fun checkReturnKey(type:String) {
        AAApp.appComponent.searchData = type
        customizeReturnKey()
    }
    override fun observeViewModel() {
    }

    override fun customizeReturnKey() {
        navigateTo(R.id.action_searchFragment_to_homeFragment)
    }
}