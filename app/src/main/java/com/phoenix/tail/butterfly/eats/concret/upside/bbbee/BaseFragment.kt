package com.phoenix.tail.butterfly.eats.concret.upside.bbbee

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

abstract class BaseFragment<DB : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    protected lateinit var binding: DB
    protected lateinit var viewModel: VM

    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(this).get(viewModelClass)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                customizeReturnKey()
            }
        })
    }

    abstract fun setupViews()

    abstract fun observeViewModel()

    abstract fun customizeReturnKey()

    protected fun navigateTo(actionId: Int) {
        try {
            findNavController().navigate(actionId)
        } catch (e: IllegalArgumentException) {
            Log.e("BaseFragment", "Navigation action not found: $actionId", e)
        }
    }


    protected fun navigateTo(actionId: Int, bundle: Bundle) {
        findNavController().navigate(actionId, bundle)
    }

    protected fun showCustomDialog(message: String, title: String = "Info") {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }
}
