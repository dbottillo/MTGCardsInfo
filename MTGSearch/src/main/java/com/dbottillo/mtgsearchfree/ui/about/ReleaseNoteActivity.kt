package com.dbottillo.mtgsearchfree.ui.about

import android.os.Bundle
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.BasicActivity

class ReleaseNoteActivity : BasicActivity(){

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.activity_release_note)

        findViewById<TextView>(R.id.release_note).setText(R.string.release_note_text_full)

    }

    override fun getPageTrack(): String? {
        return "/release-note"
    }

}