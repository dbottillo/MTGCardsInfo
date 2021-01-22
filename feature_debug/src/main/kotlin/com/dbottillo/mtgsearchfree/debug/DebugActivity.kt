package com.dbottillo.mtgsearchfree.debug

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.dbottillo.mtgsearchfree.AppPreferences
import com.dbottillo.mtgsearchfree.Navigator
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.model.helper.CreateDecksAsyncTask
import com.dbottillo.mtgsearchfree.util.PermissionAvailable
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.copyDbFromSdCard
import com.dbottillo.mtgsearchfree.util.copyDbToSdCard
import com.dbottillo.mtgsearchfree.util.request
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_debug.*
import javax.inject.Inject

class DebugActivity : DaggerAppCompatActivity() {

    @Inject lateinit var appPreferences: AppPreferences
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var cardsInfoDbHelper: CardsInfoDbHelper
    @Inject lateinit var trackingManager: TrackingManager

    private val debugInteractor by lazy(LazyThreadSafetyMode.NONE) { DebugInteractor(cardsInfoDbHelper) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        clear_preferences.setOnClickListener {
            appPreferences.clear()
        }
        create_db.setOnClickListener {
            recreateDb()
        }
        fill_decks.setOnClickListener {
            CreateDecksAsyncTask(applicationContext).execute()
        }
        create_fav.setOnClickListener {
            AddFavouritesAsyncTask(applicationContext).execute()
        }
        crash.setOnClickListener {
            throw RuntimeException("This is a crash")
        }
        send_db.setOnClickListener {
            copyDBToSdCard()
        }
        copy_db.setOnClickListener {
            val copied = applicationContext.copyDbFromSdCard(CardsInfoDbHelper.DATABASE_NAME)
            Toast.makeText(applicationContext, if (copied) "database copied" else "database not copied", Toast.LENGTH_LONG).show()
        }
        delete_favs.setOnClickListener {
            debugInteractor.deleteSavedCards()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe({
                        Toast.makeText(this@DebugActivity, "saved cards deleted", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(this@DebugActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
        }
        delete_decks.setOnClickListener {
            debugInteractor.deleteDecks()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe({
                        Toast.makeText(this@DebugActivity, "decks deleted", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(this@DebugActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
        }
    }

    private fun recreateDb() {
        requestPermission(PermissionAvailable.WriteStorage, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                CreateDBAsyncTask(applicationContext, application.packageName).execute()
            }

            override fun permissionNotGranted() {
                Toast.makeText(applicationContext, getString(R.string.error_export_db), Toast.LENGTH_SHORT).show()
            }
        })
    }

    @Suppress("MaxLineLength")
    private fun copyDBToSdCard() {
        requestPermission(PermissionAvailable.WriteStorage, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                val file = applicationContext.copyDbToSdCard(CardsInfoDbHelper.DATABASE_NAME)
                if (file != null) {
                    val snackBar = Snackbar
                            .make(findViewById(android.R.id.content), getString(R.string.db_exported), Snackbar.LENGTH_LONG)
                            .setAction(getString(com.dbottillo.mtgsearchfree.core.R.string.share)) {
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.type = "text/plain"
                                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("help@mtgcardsinfo.com"))
                                intent.putExtra(Intent.EXTRA_SUBJECT, "[MTGCardsInfo] Database status")
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                                startActivity(Intent.createChooser(intent, "Send mail...."))
                                trackingManager.trackDeckExport()
                            }
                    snackBar.show()
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

    private var permissionListener: PermissionUtil.PermissionListener? = null

    private fun requestPermission(permission: PermissionAvailable, listener: PermissionUtil.PermissionListener) {
        this.permissionListener = listener
        if (PermissionUtil.permissionGranted(this, permission)) {
            listener.permissionGranted()
            return
        }
        this.request(permission)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else{
            false
        }
    }
}