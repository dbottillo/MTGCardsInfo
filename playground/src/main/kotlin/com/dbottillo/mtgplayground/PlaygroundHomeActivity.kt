package com.dbottillo.mtgplayground

import android.annotation.SuppressLint
import android.os.Bundle
import com.dbottillo.mtgsearchfree.network.MKMApiInterface
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_playground.*
import javax.inject.Inject

class PlaygroundHomeActivity : DaggerAppCompatActivity() {

    @Inject lateinit var api: MKMApiInterface

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_playground)

        do_request.setOnClickListener {
            doRequest()
        }
    }

    @SuppressLint("CheckResult")
    private fun doRequest() {
        api.fetchProduct("Counterspell")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    output.text = it.toString()
                }, {
                    output.text = it.localizedMessage
                })
    }
}