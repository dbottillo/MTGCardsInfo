package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterPresenter
import com.dbottillo.mtgsearchfree.lifecounter.LifeCounterPresenterImpl
import com.dbottillo.mtgsearchfree.lifecounter.PlayerInteractor
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class LifeCounterModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [LifeCounterFragmentModule::class])
    abstract fun contributeLifeCounterFragmentInjector(): LifeCounterFragment
}

@Module
class LifeCounterFragmentModule {
    @Provides
    fun providesLifeCounterFragmentPresenter(
        interactor: PlayerInteractor,
        logger: Logger
    ): LifeCounterPresenter {
        return LifeCounterPresenterImpl(interactor, logger)
    }
}