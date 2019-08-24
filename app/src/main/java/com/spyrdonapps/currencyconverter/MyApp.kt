package com.spyrdonapps.currencyconverter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.util.Log
import com.spyrdonapps.currencyconverter.di.components.DaggerMyAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class MyApp : Application(), HasActivityInjector {

    @Inject
    internal lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): DispatchingAndroidInjector<Activity> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        DaggerMyAppComponent.builder().create(this).inject(this)
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.Tree() {

                @SuppressLint("LogNotTimber")
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Log.e("APP", message)
                }
            })
        }
    }
}