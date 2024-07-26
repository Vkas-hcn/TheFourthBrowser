package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.hhhee

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseViewModel
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.searchData

class HomeViewModel : BaseViewModel() {

     fun searchUrl(data: String): String {
        val urlPattern =
            "^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$".toRegex()

        return if (urlPattern.matches(data)) {
            if (data.startsWith("http://") || data.startsWith("https://")) {
                data
            } else {
                "https://$data"
            }
        } else {
            "${setSearchIcon()}${data.replace(" ", "+")}"
        }
    }


    private fun setSearchIcon():String {
       return when (AAApp.appComponent.searchData) {
            "1" -> {
                DataUtils.ser_bing
            }

            "2" -> {
                DataUtils.ser_google
            }

            "3" -> {
                DataUtils.ser_yahoo
            }

            "4" -> {
                DataUtils.ser_duck

            }
            else -> {
                DataUtils.ser_google
            }
        }
    }
    fun closeKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showDeleteDialog(context: Context,deleteFun:()->Unit){
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

    fun showClearDialog(context: Context,deleteFun:()->Unit){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tip")
        builder.setMessage("Are you sure you want to clear history?")
        builder.setPositiveButton("Yes") { _, _ ->
            deleteFun()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun shareText(context: Context) {
        val text ="https://play.google.com/store/apps/details?id=${context.packageName}"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

}