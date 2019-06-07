package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.about.AboutActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AboutModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [(BasicAboutModule::class)])
    abstract fun contributeAboutActivityInjector(): AboutActivity
}

@Module
class BasicAboutModule