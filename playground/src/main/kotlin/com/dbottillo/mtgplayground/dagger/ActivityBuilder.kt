package com.dbottillo.mtgplayground.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgplayground.PlaygroundHomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHomeActivityInjector(): PlaygroundHomeActivity
}