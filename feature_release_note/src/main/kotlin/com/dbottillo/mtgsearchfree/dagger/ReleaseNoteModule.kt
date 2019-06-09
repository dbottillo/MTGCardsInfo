package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.releasenote.ReleaseNoteActivity
import com.dbottillo.mtgsearchfree.releasenote.ReleaseNoteInteractor
import com.dbottillo.mtgsearchfree.releasenote.ReleaseNotePresenter
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReleaseNoteModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [(ReleaseNoteActivityModule::class)])
    abstract fun contributeReleaseNoteActivityInjector(): ReleaseNoteActivity
}

@Module
class ReleaseNoteActivityModule {
    @Provides
    fun providesReleaseNotePresenter(
        interactor: ReleaseNoteInteractor,
        logger: Logger
    ): ReleaseNotePresenter {
        return ReleaseNotePresenter(interactor, logger)
    }
}