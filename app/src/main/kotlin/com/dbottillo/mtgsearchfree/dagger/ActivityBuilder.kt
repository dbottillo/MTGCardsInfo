package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.debug.DebugActivity
import com.dbottillo.mtgsearchfree.home.BaseHomeFragment
import com.dbottillo.mtgsearchfree.home.HomeActivity
import com.dbottillo.mtgsearchfree.settings.SettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun contributeHomeActivityInjector(): HomeActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeBaseHomeFragmentInjector(): BaseHomeFragment

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeDebugActivityInjector(): DebugActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeSettingsActivityInjector(): SettingsActivity
}