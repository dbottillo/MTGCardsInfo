package com.dbottillo.mtgsearchfree.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.FrameLayout
import android.widget.Toast
import com.dbottillo.mtgsearchfree.INTENT_RELEASE_NOTE_PUSH
import com.dbottillo.mtgsearchfree.MTGApp
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.model.helper.CreateDBAsyncTask
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteActivity
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragment
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.ui.saved.SavedFragment
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragment
import com.dbottillo.mtgsearchfree.ui.views.BottomTabs
import com.dbottillo.mtgsearchfree.util.*
import java.lang.ref.WeakReference

class HomeActivity : BasicActivity() {

    private var fragments = mutableMapOf<String, WeakReference<BaseHomeFragment>>()

    private val bottomTabs: BottomTabs by bind(R.id.bottom_tabs)
    private val fragmentContainer: FrameLayout by bind(R.id.fragment_container)
    private var bottomTabsHeight: Int = 0
    private var currentBottomTabsHeightAnimator: ValueAnimator? = null
    private var isUserScrollingDown: Boolean = false

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_home)

        bottomTabsHeight = resources.getDimensionPixelSize(R.dimen.bottom_tabs_height)

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
            checkAndReplace("sets")
        }

        if (intent != null && intent.hasExtra(INTENT_RELEASE_NOTE_PUSH)) {
            startActivity(Intent(this, ReleaseNoteActivity::class.java))
            intent.putExtra(INTENT_RELEASE_NOTE_PUSH, false)
        }

    }

    private fun checkAndReplace(tag: String) {
        changeFragment(getFragmentFromTag(tag), tag, false)
    }

    override fun getPageTrack(): String {
        return "/home"
    }

    private fun getFragmentFromTag(tag: String): BaseHomeFragment {
        return when (tag) {
            "sets" -> fragments[tag]?.get() ?: createAndSet(tag, SetsFragment())
            "decks" -> fragments[tag]?.get() ?: createAndSet(tag, DecksFragment())
            "saved" -> fragments[tag]?.get() ?: createAndSet(tag, SavedFragment())
            else -> fragments[tag]?.get() ?: createAndSet(tag, LifeCounterFragment())
        }
    }

    private fun createAndSet(tag: String, fragment: BaseHomeFragment): BaseHomeFragment {
        fragments.put(tag, WeakReference(fragment))
        return fragment
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is SetsFragment) {
            checkAndReplace("sets")
            bottomTabs.setSelection(0)
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
        currentBottomTabsHeightAnimator = ValueAnimator.ofInt(bottomTabs.height, targetHeight)
        currentBottomTabsHeightAnimator?.addUpdateListener { valueAnimator ->
            bottomTabs.setHeight(valueAnimator.animatedValue as Int)
        }
        currentBottomTabsHeightAnimator?.duration = 100
        currentBottomTabsHeightAnimator?.start()
    }

    fun recreateDb() {
        requestPermission(PermissionAvailable.WriteStorage, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                CreateDBAsyncTask(applicationContext, application.packageName).execute()
            }

            override fun permissionNotGranted() {
                Toast.makeText(applicationContext, getString(R.string.error_export_db), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun copyDBToSdCard() {
        requestPermission(PermissionAvailable.WriteStorage, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                val file = applicationContext.copyDbToSdCard(CardsInfoDbHelper.DATABASE_NAME)
                if (file != null) {
                    val snackbar = Snackbar
                            .make(fragmentContainer, getString(R.string.db_exported), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.share)) {
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.type = "text/plain"
                                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("help@mtgcardsinfo.com"))
                                intent.putExtra(Intent.EXTRA_SUBJECT, "[MTGCardsInfo] Database status")
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                                startActivity(Intent.createChooser(intent, "Send mail...."))
                                TrackingManager.trackDeckExport()
                            }
                    snackbar.show()
                } else {
                    Toast.makeText(applicationContext, getString(R.string.error_export_db), Toast.LENGTH_SHORT).show()
                }
            }

            override fun permissionNotGranted() {
                Toast.makeText(applicationContext, getString(R.string.error_export_db), Toast.LENGTH_SHORT).show()
            }
        })
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
}
