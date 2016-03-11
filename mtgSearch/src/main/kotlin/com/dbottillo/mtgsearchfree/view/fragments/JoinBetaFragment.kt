package com.dbottillo.mtgsearchfree.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.dbottillo.mtgsearchfree.R

class JoinBetaFragment : BasicFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_about, container, false)

        ButterKnife.bind(this, v)

        setActionBarTitle(getString(R.string.action_about))

        return v
    }


    override fun getPageTrack(): String? {
        return "/about"
    }

}
