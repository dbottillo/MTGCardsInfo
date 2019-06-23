package com.dbottillo.mtgsearchfree.home

import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.Toast
import com.dbottillo.mtgsearchfree.Constants.INTENT_RELEASE_NOTE_PUSH
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.setHeight
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.ref.WeakReference

class HomeActivity : BasicActivity() {

    private var fragments = mutableMapOf<String, WeakReference<BasicFragment>>()

    private var bottomTabsHeight: Int = 0
    private var currentBottomTabsHeightAnimator: ValueAnimator? = null
    private var isUserScrollingDown: Boolean = false

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_home)

        bottomTabsHeight = resources.getDimensionPixelSize(R.dimen.bottom_tabs_height)

        bottom_tabs.setBottomTabsListener(object : BottomTabs.BottomTabsListener {
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
            checkAndReplace("sets")
        }

        if (intent != null && intent.hasExtra(INTENT_RELEASE_NOTE_PUSH)) {
            navigator.openReleaseNoteScreen(this)
            intent.putExtra(INTENT_RELEASE_NOTE_PUSH, false)
        }
    }

    private fun checkAndReplace(tag: String) {
        changeFragment(getFragmentFromTag(tag), tag, false)
    }

    override fun getPageTrack(): String {
        return "/home"
    }

    private fun getFragmentFromTag(tag: String): BasicFragment {
        return when (tag) {
            "sets" -> fragments[tag]?.get() ?: createAndSet(tag, navigator.newSetsCounterFragment())
            "decks" -> fragments[tag]?.get() ?: createAndSet(tag, navigator.newDecksFragment())
            "saved" -> fragments[tag]?.get() ?: createAndSet(tag, navigator.newSavedFragment())
            else -> fragments[tag]?.get() ?: createAndSet(tag, navigator.newLifeCounterFragment())
        }
    }

    private fun createAndSet(tag: String, fragment: BasicFragment): BasicFragment {
        fragments[tag] = WeakReference(fragment)
        return fragment
    }

    override fun onBackPressed() {
        if (!navigator.isSetsFragment(supportFragmentManager.findFragmentById(R.id.fragment_container))) {
            checkAndReplace("sets")
            bottom_tabs.setSelection(0)
        } else {
            super.onBackPressed()
        }
    }

    fun scrollingUp() {
        if (!isUserScrollingDown) {
            return
        }

        animateBottomTabs(0)
        isUserScrollingDown = false
    }

    fun scrollingDown() {
        if (isUserScrollingDown) {
            return
        }
        animateBottomTabs(bottomTabsHeight)
        isUserScrollingDown = true
    }

    private fun animateBottomTabs(targetHeight: Int) {
        currentBottomTabsHeightAnimator?.cancel()
        currentBottomTabsHeightAnimator = ValueAnimator.ofInt(bottom_tabs.height, targetHeight)
        currentBottomTabsHeightAnimator?.addUpdateListener { valueAnimator ->
            bottom_tabs.setHeight(valueAnimator.animatedValue as Int)
        }
        currentBottomTabsHeightAnimator?.duration = 100
        currentBottomTabsHeightAnimator?.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.isGranted(grantResults)) {
            copyDBToSdCard()
        } else {
            Toast.makeText(this, getString(R.string.error_export_db), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragments.clear()
    }

    private fun changeFragment(fragment: BasicFragment, tag: String, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commit()
    }
}