package com.dbottillo.mtgsearchfree

import android.app.Activity
import android.app.Application
import android.os.StrictMode
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics
import com.dbottillo.mtgsearchfree.dagger.DaggerAppComponent
import com.dbottillo.mtgsearchfree.dagger.DataModule
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.LOG
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

open class MTGApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject lateinit var cardsPreferences: CardsPreferences
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

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

            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
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