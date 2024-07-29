package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.ssstt

import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.backHome
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.clickInt
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.open
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentStartBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StartFragment : BaseFragment<FragmentStartBinding, StartViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_start

    override val viewModelClass: Class<StartViewModel>
        get() = StartViewModel::class.java

    private lateinit var disposable: Disposable
    private lateinit var finishDisposable: Disposable

    override fun setupViews() {
        startCountdown()
        AdManager.loadAd(open)
        AdManager.loadAd(clickInt)
        AdManager.loadAd(backHome)
    }

    override fun observeViewModel() {
        viewModel.liveHomeData.observe(viewLifecycleOwner) {
            if (it) {
                stopRotation(binding.imgLoading)
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
        rotateImage(binding.imgLoading)
        disposable = Observable.intervalRange(0, count, 0, intervalTime, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { progress ->
                val progressPercentage = ((progress + 1).toFloat() / count.toFloat() * 100).toInt()
                if (progressPercentage > 28 && AdManager.jumFunAd(open)) {
                    disposable.dispose()
                    finishDisposable.dispose()
                    viewModel.liveHomeData.postValue(true)
                    return@subscribe
                }
                if (progressPercentage > 28 && AdManager.canShowAd(open)) {
                    Log.e("TAG", "startCountdown:show")
                    AdManager.showAd(open, requireActivity()) {
                        viewModel.liveHomeData.postValue(true)
                    }
                    disposable.dispose()
                    finishDisposable.dispose()  // Ensure the finish timer is also disposed
                }
            }

        finishDisposable = Completable.timer(totalTime, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("TAG", "startCountdown:cao shi")

                viewModel.liveHomeData.postValue(true)
            }
    }
    private fun rotateImage(imageView: ImageView) {
        val animator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0f, 360f).apply {
            this.duration = 900
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
        animator.start()
        imageView.tag = animator
    }
    private fun stopRotation(imageView: ImageView) {
        val animator = imageView.tag as? ObjectAnimator
        animator?.cancel()
    }
    override fun onDestroy() {
        super.onDestroy()
        // Dispose both disposables when the activity/fragment is destroyed
        disposable.dispose()
        finishDisposable.dispose()
    }
}