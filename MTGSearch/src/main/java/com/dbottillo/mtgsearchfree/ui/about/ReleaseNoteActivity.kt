package com.dbottillo.mtgsearchfree.ui.about

import android.os.Bundle
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.BasicActivity

class ReleaseNoteActivity : BasicActivity(){

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.activity_release_note)

        (findViewById(R.id.release_note) as TextView).text = getText(R.string.release_note_text_full)

    }

    override fun getPageTrack(): String? {
        return "/release-note"
    }

}