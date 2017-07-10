package com.dbottillo.mtgsearchfree.ui.about

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import java.util.*

class AboutActivity : BasicActivity(), View.OnTouchListener{

    internal var librariesName = arrayOf("Smooth Progress Bar", "Picasso", "LeakMemory")
    internal var librariesAuthor = arrayOf("Castorflex", "Square", "Square")
    internal var librariesLink = arrayOf("https://github.com/castorflex/SmoothProgressBar", "http://square.github.io/picasso/", "https://github.com/square/leakcanary")

    internal var versionName: String = ""
    internal lateinit var versionText : TextView
    internal var firstTap: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.activity_about)

        mtgApp.uiGraph.inject(this)

        versionText = findViewById(R.id.about_version) as TextView
        try {
            versionName = packageManager.getPackageInfo(packageName, 0).versionName
            versionText.text = Html.fromHtml("<b>" + getString(R.string.version) + "</b>: " + versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            LOG.e(e)
        }

        versionText.setOnTouchListener(this)
        findViewById(R.id.send_feedback).setOnClickListener{
            sendFeedback()
        }

        findViewById(R.id.share_app).setOnClickListener(View.OnClickListener {
            TrackingManager.trackShareApp()
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            val url = "https://play.google.com/store/apps/details?id=com.dbottillo.mtgsearchfree"
            i.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(i, getString(R.string.share)))
        })

        val cardContainer = findViewById(R.id.libraries_container) as LinearLayout

        for (i in librariesName.indices) {
            val libraryView = View.inflate(this, R.layout.row_library, null)
            val title = libraryView.findViewById(R.id.library_name) as TextView
            title.setText(librariesName[i])
            val author = libraryView.findViewById(R.id.library_author) as TextView
            author.setText(librariesAuthor[i])
            cardContainer.addView(libraryView)
            libraryView.tag = 0
            libraryView.setOnClickListener { v ->
                val tag = v.tag as Int
                val uri = Uri.parse(librariesLink[tag])
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                startActivity(intent)
                TrackingManager.trackAboutLibrary(librariesLink[0])
            }
        }

        (findViewById(R.id.copyright) as TextView).text = getString(R.string.copyright)
    }

    private fun sendFeedback() {
        LOG.d()
        TrackingManager.trackOpenFeedback()
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.email), null))
        val text = String.format(getString(R.string.feedback_text), versionName,
                Build.VERSION.SDK_INT.toString(), Build.DEVICE, Build.MODEL, Build.PRODUCT)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback) + " " + getString(R.string.app_name))
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(text))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, ev: MotionEvent): Boolean {

        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> firstTap = Calendar.getInstance().timeInMillis
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                val diff = Calendar.getInstance().timeInMillis - firstTap
                val seconds = diff / 1000
                if (seconds < 5) {
                    versionText.setOnTouchListener(null)
                    generalData.setDebug()
                    Toast.makeText(this, R.string.debug_mode_active, Toast.LENGTH_LONG).show()
                }
                v.performClick()
            }
            MotionEvent.ACTION_MOVE -> {
            }
            else -> {
            }
        }
        return true
    }

    override fun getPageTrack(): String? {
        return "/about"
    }

}