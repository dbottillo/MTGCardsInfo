package com.dbottillo.mtgsearchfree.component

import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.modules.AndroidModule
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidModule::class))
interface AndroidComponent {
    fun inject(application: MTGApp)

    fun inject(mainActivity: BasicActivity)
}