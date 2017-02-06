package com.dbottillo.mtgsearchfree.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.decks.NewDecksFragment
import com.dbottillo.mtgsearchfree.ui.lifecounter.NewLifeCounterFragment
import com.dbottillo.mtgsearchfree.ui.saved.NewSavedFragment
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragment
import com.dbottillo.mtgsearchfree.ui.views.BottomTabs
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.UIUtil
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment

class HomeActivity : BasicActivity() {

    @BindView(R.id.bottom_tabs)
    lateinit var bottomTabs: BottomTabs

    @BindView(R.id.fragment_container)
    lateinit var fragmentContainer: FrameLayout

    var bottomTabsHeight : Int = 0
    var currentBottomTabsHeightAnimator: ValueAnimator? = null
    var isUserScrollingDown: Boolean = false

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)

        bottomTabsHeight = resources?.getDimensionPixelSize(R.dimen.bottom_tabs_height)!!

        bottomTabs.setBottomTabsListener(object : BottomTabs.BottomTabsListener {
            override fun tabSelected(selection: Int) {
                when (selection) {
                    0 -> checkAndReplace("sets")
                    1 -> checkAndReplace("decks")
                    2 -> checkAndReplace("saved")
                    3 -> checkAndReplace("life")
                }
            }
        })

        if (bundle == null) {
            changeFragment(SetsFragment(), "sets", false)
        }

    }

    private fun checkAndReplace(tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            changeFragment(getFragmentFromTag(tag), tag, false)
        }
    }

    override fun getPageTrack(): String {
        return "/home"
    }

    private fun getFragmentFromTag(tag: String): BasicFragment {
        if (tag == "sets") {
            return SetsFragment()
        }
        if (tag == "decks") {
            return NewDecksFragment()
        }
        if (tag == "saved") {
            return NewSavedFragment()
        }
        // life
        return NewLifeCounterFragment()
    }

    fun scrollingUp(){
        if (!isUserScrollingDown){
            return
        }

        animateBottomTabs(0)
        isUserScrollingDown = false
    }

    fun scrollingDown(){
        if (isUserScrollingDown){
            return
        }
        animateBottomTabs(bottomTabsHeight)
        isUserScrollingDown = true
    }

    internal fun animateBottomTabs(targetHeight : Int){
        currentBottomTabsHeightAnimator?.cancel()
        currentBottomTabsHeightAnimator = ValueAnimator.ofInt(bottomTabs.height, targetHeight)
        currentBottomTabsHeightAnimator?.addUpdateListener { valueAnimator ->
            UIUtil.setHeight(bottomTabs, valueAnimator.animatedValue as Int)
        }
        currentBottomTabsHeightAnimator?.duration = 100
        currentBottomTabsHeightAnimator?.start()
    }

}
