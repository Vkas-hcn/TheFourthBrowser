package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.ssstt

import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentStartBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StartFragment : BaseFragment<FragmentStartBinding, StartViewModel>(){
    override val layoutId: Int
        get() = R.layout.fragment_start

    override val viewModelClass: Class<StartViewModel>
        get() = StartViewModel::class.java

    private lateinit var disposable: Disposable
    private lateinit var finishDisposable: Disposable

    override fun setupViews() {
        startCountdown()
    }

    override fun observeViewModel() {
        viewModel.liveHomeData.observe(viewLifecycleOwner) {
            if (it) {
                navigateTo(R.id.action_startFragment_to_homeFragment)
            }
        }
    }

    override fun customizeReturnKey() {

    }

    private fun startCountdown() {
        val totalTime = 14000L
        val intervalTime = 700L
        val count = totalTime / intervalTime
        disposable = Observable.intervalRange(0, count, 0, intervalTime, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { progress ->
                val progressPercentage = ((progress + 1).toFloat() / count.toFloat() * 100).toInt()
                if(progressPercentage>28 || viewModel.isFinish){
                    disposable.dispose()
                    viewModel.liveHomeData.postValue(true)
                }
            }

        finishDisposable = Completable.timer(totalTime, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewModel.liveHomeData.postValue(true)
            }
    }
}