package com.dbottillo.mtgsearchfree.toolbarereveal

import android.os.Bundle
import android.view.View

import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.BasicFragment

/**
 * Abstract class that let fragments access the [ToolbarRevealScrollHelper] seamless
 */
abstract class ToolbarRevealScrollFragment : BasicFragment() {

    private lateinit var toolbarRevealScrollHelper: ToolbarRevealScrollHelper

    /**
     * The concrete fragment needs to provide the scrollview id of its content
     *
     * @return id of the scrollview
     */
    abstract val scrollViewId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarRevealScrollHelper = ToolbarRevealScrollHelper(this, scrollViewId,
                R.color.white, heightToolbar, isStatusBarIncludedInReveal())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarRevealScrollHelper.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        toolbarRevealScrollHelper.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        toolbarRevealScrollHelper.onResume()
    }

    override fun onPause() {
        super.onPause()
        toolbarRevealScrollHelper.onPause()
    }

    open fun isStatusBarIncludedInReveal() = true
}
