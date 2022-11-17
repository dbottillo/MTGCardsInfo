package com.dbottillo.mtgsearchfree.home

import android.os.Bundle
import com.dbottillo.mtgsearchfree.AppPreferences
import com.dbottillo.mtgsearchfree.home.databinding.ActivityHomeBinding
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.dbottillo.mtgsearchfree.util.show
import java.lang.ref.WeakReference
import javax.inject.Inject

class HomeActivity : BasicActivity() {

    private var fragments = mutableMapOf<String, WeakReference<BasicFragment>>()

    private var bottomTabsHeight: Int = 0

    @Inject lateinit var appPreferences: AppPreferences

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        bottomTabsHeight = resources.getDimensionPixelSize(R.dimen.bottom_tabs_height)

        binding.bottomTabs.setBottomTabsListener(object : BottomTabs.BottomTabsListener {
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

        binding.updateBanner.show()
        binding.bannerShadow.show()
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
            binding.bottomTabs.setSelection(0)
        } else {
            super.onBackPressed()
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