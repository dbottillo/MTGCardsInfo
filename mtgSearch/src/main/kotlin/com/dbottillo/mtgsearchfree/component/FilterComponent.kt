package com.dbottillo.mtgsearchfree.component

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.modules.CardFilterModule
import com.dbottillo.mtgsearchfree.view.activities.MainActivity
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(CardFilterModule::class), dependencies = arrayOf(AppComponent::class))
interface FilterComponent {
    fun inject(activity: MainActivity)
}