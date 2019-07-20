package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.saved.SavedCardsPresenter
import com.dbottillo.mtgsearchfree.saved.SavedCardsPresenterImpl
import com.dbottillo.mtgsearchfree.saved.SavedFragment
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class SavedFragmentBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [SavedFragmentModule::class])
    abstract fun contributeSavedFragmentInjector(): SavedFragment
}

@Module
class SavedFragmentModule {
    @Provides
    fun providesSavedFragmentPresenter(
        interactor: SavedCardsInteractor,
        generalData: GeneralData,
        logger: Logger
    ): SavedCardsPresenter {
        return SavedCardsPresenterImpl(interactor, generalData, logger)
    }
}