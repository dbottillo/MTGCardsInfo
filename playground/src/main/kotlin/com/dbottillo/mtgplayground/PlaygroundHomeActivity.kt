package com.dbottillo.mtgsearchfree

import android.os.Bundle
import com.dbottillo.mtgplayground.R
import dagger.android.support.DaggerAppCompatActivity

class PlaygroundHomeActivity : DaggerAppCompatActivity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_playground)
    }
}