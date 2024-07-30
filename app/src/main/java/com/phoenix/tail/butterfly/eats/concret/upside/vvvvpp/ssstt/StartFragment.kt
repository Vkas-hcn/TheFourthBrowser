package com.phoenix.tail.butterfly.eats.concret.upside.vvvvpp.ssstt

import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.phoenix.tail.butterfly.eats.concret.upside.R
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.AAApp
import com.phoenix.tail.butterfly.eats.concret.upside.bbbee.BaseFragment
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.bbbbhhee
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.cccckkii
import com.phoenix.tail.butterfly.eats.concret.upside.bbbnn.AdManager.oooonn
import com.phoenix.tail.butterfly.eats.concret.upside.databinding.FragmentStartBinding
import com.phoenix.tail.butterfly.eats.concret.upside.uuuuss.DataUtils.ccccmmttt
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
        postProgress()
        AdManager.loadAd(oooonn)
        AdManager.loadAd(cccckkii)
        AdManager.loadAd(bbbbhhee)
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

    private fun isCMPType(): Boolean {
        return AAApp.appComponent.ccccmmttt == "cmp"
    }

    private fun postProgress() {
        GlobalScope.launch(Dispatchers.Main) {
            UUUUOOOGG()
            while (isActive) {
                if (isCMPType()) {
                    disposable.dispose()
                    finishDisposable.dispose()
                    cancel()
                    startCountdown()
                    return@launch
                }
                delay(500)
            }
        }
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
                if (progressPercentage > 28 && AdManager.jumFunAd(oooonn) && isCMPType()) {
                    disposable.dispose()
                    finishDisposable.dispose()
                    viewModel.liveHomeData.postValue(true)
                    return@subscribe
                }
                if (progressPercentage > 28 && AdManager.canShowAd(oooonn) && isCMPType()) {
                    Log.e("TAG", "startCountdown:show")
                    activity?.let {
                        AdManager.showAd(oooonn, it) {
                            viewModel.liveHomeData.postValue(true)
                        }
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
                if (isCMPType()) {
                    viewModel.liveHomeData.postValue(true)
                }
            }
    }

    private fun UUUUOOOGG() {
        if (AAApp.appComponent.ccccmmttt == "cmp") {
            return
        }
        val debugSettings =
            activity?.let {
                ConsentDebugSettings.Builder(it)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId("AC7B1FBB9020A505095E8013EA6117B8")
                    .build()
            }
        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        val consentInformation: ConsentInformation? = activity?.let {
            UserMessagingPlatform.getConsentInformation(
                it
            )
        }
        activity?.let {
            consentInformation?.requestConsentInfoUpdate(
                it,
                params, {
                    activity?.let { it1 ->
                        UserMessagingPlatform.loadAndShowConsentFormIfRequired(it1) {
                            if (consentInformation.canRequestAds()) {
                                AAApp.appComponent.ccccmmttt = "cmp"
                            }
                        }
                    }
                },
                {
                    AAApp.appComponent.ccccmmttt = "cmp"
                }
            )
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