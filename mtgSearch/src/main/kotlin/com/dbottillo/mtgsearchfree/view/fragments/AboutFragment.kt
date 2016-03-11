package com.dbottillo.mtgsearchfree.view.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.Uri.parse
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.persistence.GeneralPreferences
import com.dbottillo.mtgsearchfree.tracking.TrackingManager
import java.util.*

class AboutFragment : BasicFragment(), View.OnClickListener, View.OnTouchListener {

    internal var librariesName = arrayOf("Smooth Progress Bar", "Picasso", "LeakMemory")
    internal var librariesAuthor = arrayOf("Castorflex", "Square", "Square")
    internal var librariesLink = arrayOf("https://github.com/castorflex/SmoothProgressBar", "http://square.github.io/picasso/", "https://github.com/square/leakcanary")

    var versionName: String = ""
    private var firstTap: Long = 0

    val shareApp: View by bindView(R.id.share_app)
    val version: TextView by bindView(R.id.about_version)
    val copyright: TextView by bindView(R.id.copyright)
    val cardContainer: LinearLayout by bindView(R.id.libraries_container)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_about, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBarTitle(getString(R.string.action_about))

        try {
            versionName = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
            version.text = Html.fromHtml("<b>" + getString(R.string.version) + "</b>: " + versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            LOG.d("[AboutFragment] exception: " + e.message);
        }

        shareApp.setOnClickListener {
            TrackingManager.trackShareApp();
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            val url = "https://play.google.com/store/apps/details?id=com.dbottillo.mtgsearchfree"
            i.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(i, getString(R.string.share)))
        }

        for (i in librariesName.indices) {
            val libraryView = View.inflate(context, R.layout.row_library, null)
            val title = libraryView.findViewById(R.id.library_name) as TextView
            title.text = librariesName[i]
            val author = libraryView.findViewById(R.id.library_author) as TextView
            author.text = librariesAuthor[i]
            cardContainer.addView(libraryView)
            libraryView.tag = 0
            libraryView.setOnClickListener { v ->
                val tag = v.tag as Int
                val uri = parse(librariesLink[tag])
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                startActivity(intent)
                TrackingManager.trackAboutLibrary(librariesLink[0]);
            }
        }

        if (!GeneralPreferences.with(activity.applicationContext).isDebugEnabled) {
            version.setOnTouchListener(this)
        }

        copyright.text = getString(R.string.copyright)
    }

    @OnClick(R.id.send_feedback)
    override fun onClick(v: View) {
        TrackingManager.trackFeedback()
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", activity.getString(R.string.email), null))
        val text = String.format(getString(R.string.feedback_text), versionName,
                Build.VERSION.SDK_INT, Build.DEVICE, Build.MODEL, Build.PRODUCT)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback) + " " + activity.getString(R.string.app_name))
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(text))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)))
    }

    override fun getPageTrack(): String? {
        return "/about"
    }

    override fun onTouch(v: View, ev: MotionEvent): Boolean {
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> firstTap = Calendar.getInstance().timeInMillis
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                val diff = Calendar.getInstance().timeInMillis - firstTap
                val seconds = diff / 1000
                if (seconds < 5) {
                    version.setOnTouchListener(null)
                    GeneralPreferences.with(activity.applicationContext).setDebug()
                    Toast.makeText(activity, R.string.debug_mode_active, Toast.LENGTH_LONG).show()
                }
            }
            MotionEvent.ACTION_MOVE -> {
            }
            else -> {
            }
        }
        return true
    }

}
