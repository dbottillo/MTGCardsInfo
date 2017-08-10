package com.dbottillo.mtgsearchfree.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.FrameLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.model.helper.CreateDBAsyncTask
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragment
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.ui.saved.SavedFragment
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragment
import com.dbottillo.mtgsearchfree.ui.views.BottomTabs
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.UIUtil

class HomeActivity : BasicActivity() {

    lateinit var bottomTabs: BottomTabs
    lateinit var fragmentContainer: FrameLayout

    var bottomTabsHeight: Int = 0
    var currentBottomTabsHeightAnimator: ValueAnimator? = null
    var isUserScrollingDown: Boolean = false

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_home)

        fragmentContainer = findViewById<FrameLayout>(R.id.fragment_container)
        bottomTabsHeight = resources?.getDimensionPixelSize(R.dimen.bottom_tabs_height)!!

        bottomTabs = findViewById<BottomTabs>(R.id.bottom_tabs)
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
            return DecksFragment()
        }
        if (tag == "saved") {
            return SavedFragment()
        }
        // life
        return LifeCounterFragment()
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

    internal fun animateBottomTabs(targetHeight: Int) {
        currentBottomTabsHeightAnimator?.cancel()
        currentBottomTabsHeightAnimator = ValueAnimator.ofInt(bottomTabs.height, targetHeight)
        currentBottomTabsHeightAnimator?.addUpdateListener { valueAnimator ->
            UIUtil.setHeight(bottomTabs, valueAnimator.animatedValue as Int)
        }
        currentBottomTabsHeightAnimator?.duration = 100
        currentBottomTabsHeightAnimator?.start()
    }

    fun recreateDb() {
        requestPermission(PermissionUtil.TYPE.WRITE_STORAGE, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                CreateDBAsyncTask(applicationContext, application.packageName).execute()
            }

            override fun permissionNotGranted() {
                Toast.makeText(applicationContext, getString(R.string.error_export_db), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun copyDBToSdCard() {
        requestPermission(PermissionUtil.TYPE.WRITE_STORAGE, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                val file = FileUtil.copyDbToSdCard(applicationContext, CardsInfoDbHelper.DATABASE_NAME)
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

}
