package com.dbottillo.mtgsearchfree.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R

class ReleaseNoteFragment : BasicFragment() {

    val releaseNote: TextView by bindView(R.id.release_note)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_release_note, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle(getString(R.string.action_release_note))
        releaseNote.text = getText(R.string.release_note_text_full)
    }

    override fun getPageTrack(): String? {
        return "/release-note"
    }

}
