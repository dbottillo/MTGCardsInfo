package com.dbottillo.mtgplayground

import android.annotation.SuppressLint
import android.os.Bundle
import com.dbottillo.mtgplayground.databinding.ActivityPlaygroundBinding
import com.dbottillo.mtgsearchfree.network.MKMApiInterface
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PlaygroundHomeActivity : DaggerAppCompatActivity() {

    @Inject lateinit var api: MKMApiInterface

    private lateinit var binding: ActivityPlaygroundBinding

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        binding = ActivityPlaygroundBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.doRequest.setOnClickListener {
            doRequest()
        }
    }

    @SuppressLint("CheckResult")
    private fun doRequest() {
        /*api.findProduct("Petty+Theft").
                flatMap { api.findProduct(it.product!!.first().idProduct!!) }*/
            api.findProduct(403014)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.output.text = it.toString()
                }, {
                    binding.output.text = it.localizedMessage
                })
    }
}