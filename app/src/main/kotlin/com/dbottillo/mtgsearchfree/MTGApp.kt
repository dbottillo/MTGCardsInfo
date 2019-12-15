package com.dbottillo.mtgsearchfree

import android.app.Application
import android.os.StrictMode
import com.crashlytics.android.Crashlytics
import com.dbottillo.mtgsearchfree.dagger.DaggerAppComponent
import com.dbottillo.mtgsearchfree.dagger.DataModule
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.LOG
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

open class MTGApp : Application(), HasAndroidInjector {

    @Inject lateinit var cardsPreferences: CardsPreferences
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        LOG.d("============================================")
        LOG.d("            MTGApp created")
        LOG.d("============================================")

        initDagger()

        if (!isTesting()) {
            Fabric.with(this, Crashlytics())

            if (BuildConfig.DEBUG) {
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build())
            }
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    protected open fun isTesting(): Boolean {
        return false
    }

    protected open fun generateDataModule(): DataModule {
        return DataModule()
    }

    private fun initDagger() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
    }
}