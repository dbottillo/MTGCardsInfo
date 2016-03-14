package com.dbottillo.mtgsearchfree.component

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.modules.PresentersModule
import com.dbottillo.mtgsearchfree.view.activities.CardLuckyActivity
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity
import com.dbottillo.mtgsearchfree.view.activities.CommonCardsActivity
import com.dbottillo.mtgsearchfree.view.activities.MainActivity
import com.dbottillo.mtgsearchfree.view.fragments.MainFragment
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(PresentersModule::class), dependencies = arrayOf(AppComponent::class))
interface DataComponent {
    fun inject(activity: MainActivity)

    fun inject(cardsActivity: CardsActivity)

    fun inject(luckyActivity: CardLuckyActivity)

    fun inject(mainFragment: MainFragment)
}