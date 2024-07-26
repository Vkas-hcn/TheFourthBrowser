package com.phoenix.tail.butterfly.eats.concret.upside.bbbee
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    fun launchOnIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }

    suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Main) {
            block()
        }
    }

    suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.IO) {
            block()
        }
    }
}
