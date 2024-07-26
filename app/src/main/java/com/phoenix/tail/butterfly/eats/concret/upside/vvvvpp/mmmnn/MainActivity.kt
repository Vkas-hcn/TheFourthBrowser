package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.mmmnn

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseActivity
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(){
    override val layoutId: Int
        get() = R.layout.activity_main

    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    lateinit var navController: NavController

    override fun setupViews() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun observeViewModel() {
    }

    fun showStartFragment() {
        navController.navigate(R.id.startFragment)
    }
}