package com.dbottillo.mtgsearchfree.about

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.Constants
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.addBold
import com.dbottillo.mtgsearchfree.util.toHtml
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_about.*
import java.util.Calendar

class AboutActivity : BasicActivity(), View.OnTouchListener {

    private var libraries = listOf(
            DevLibrary("Picasso", "Square", "http://square.github.io/picasso/"),
            DevLibrary("LeakMemory", "Square", "https://github.com/square/leakcanary"),
            DevLibrary("RxJava", "ReactiveX", "https://github.com/ReactiveX/RxJava"))

    private lateinit var versionName: String
    private var firstTap: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(bundle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(bundle)

        setContentView(R.layout.activity_about)

        try {
            versionName = packageManager.getPackageInfo(packageName, 0).versionName
            about_version.text = SpannableStringBuilder().apply {
                addBold(getString(R.string.version))
                append(" ")
                append(versionName)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LOG.e(e)
        }

        about_version.setOnTouchListener(this)
        findViewById<View>(R.id.send_feedback).setOnClickListener {
            sendFeedback()
        }

        findViewById<View>(R.id.join_telegram).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TELEGRAM_LINK)))
        }

        findViewById<View>(R.id.privacy_policy).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PRIVACY_POLICY)))
        }

        findViewById<View>(R.id.share_app).setOnClickListener {
            TrackingManager.trackShareApp()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.dbottillo.mtgsearchfree")
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }

        val cardContainer = findViewById<LinearLayout>(R.id.libraries_container)

        libraries.forEach { library ->
            val libraryView = View.inflate(this, R.layout.row_library, null)
            val title = libraryView.findViewById<TextView>(R.id.library_name)
            title.text = library.name
            val author = libraryView.findViewById<TextView>(R.id.library_author)
            author.text = library.author
            cardContainer.addView(libraryView)
            libraryView.setOnClickListener {
                val uri = Uri.parse(library.link)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                startActivity(intent)
                TrackingManager.trackAboutLibrary(library.link)
            }
        }
    }

    private fun sendFeedback() {
        LOG.d()
        TrackingManager.trackOpenFeedback()
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.email), null))
        val text = String.format(getString(R.string.feedback_text), versionName,
                Build.VERSION.SDK_INT.toString(), Build.DEVICE, Build.MODEL, Build.PRODUCT)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback) + " " + getString(R.string.app_name))
        emailIntent.putExtra(Intent.EXTRA_TEXT, text.toHtml())
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
                    about_version.setOnTouchListener(null)
                    generalData.setDebug()
                    Toast.makeText(this, R.string.debug_mode_active, Toast.LENGTH_LONG).show()
                }
                v.performClick()
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

internal class DevLibrary(val name: String, val author: String, val link: String)