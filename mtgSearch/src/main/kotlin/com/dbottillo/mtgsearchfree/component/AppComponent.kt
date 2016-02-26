package com.dbottillo.mtgsearchfree.component

import android.content.SharedPreferences
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage
import com.dbottillo.mtgsearchfree.modules.AndroidModule
import com.dbottillo.mtgsearchfree.modules.DataModule
import com.dbottillo.mtgsearchfree.modules.InteractorsModule
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidModule::class, DataModule::class, InteractorsModule::class))
interface AppComponent {

    fun getCardFilterInteractor(): CardFilterInteractor

    fun getCardFilterStorage(): CardFilterStorage

    fun getSharedPreferences(): SharedPreferences

    fun inject(application: MTGApp)

    fun inject(mainActivity: BasicActivity)

}