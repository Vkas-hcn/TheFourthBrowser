package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.mmmmbbbkk

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.phoenix.tail.butterfly.eats.concret.upside.BuildConfig
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseViewModel

class MarkBookViewModel: BaseViewModel() {
    fun showDeleteDialog(context: Context, deleteFun:()->Unit){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tip")
        builder.setMessage("Are you sure you want to delete bookmark?")
        builder.setPositiveButton("Yes") { _, _ ->
            deleteFun()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}