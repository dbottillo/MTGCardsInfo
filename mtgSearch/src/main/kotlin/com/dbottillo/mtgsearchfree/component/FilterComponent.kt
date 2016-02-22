package com.dbottillo.mtgsearchfree.component

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.modules.CardFilterModule
import com.dbottillo.mtgsearchfree.modules.InteractorsModule
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(CardFilterModule::class), dependencies = arrayOf(AndroidComponent::class, InteractorsModule::class))
interface FilterComponent {
    fun getFilterPresenter(): CardFilterPresenter
}