package com.dbottillo.mtgsearchfree.model.network

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.TCGPrice
import com.dbottillo.mtgsearchfree.util.LOG

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

import java.net.HttpURLConnection
import java.net.URL

class NetworkIntentService : IntentService("NetworkIntentService") {

    override fun onHandleIntent(intent: Intent) {
        var res: TCGPrice? = null
        val extras = intent.extras
        val params = extras?.getParcelable<Bundle>(EXTRA_PARAMS)
        var cardName = ""
        var setName: String? = ""
        var idRequest: String? = ""

        if (params != null) {
            cardName = params.getString(EXTRA_CARD_NAME, "")
            setName = params.getString(EXTRA_SET_NAME, "").replace(" ", "%20")
            idRequest = params.getString(EXTRA_ID)
            cardName = cardName.replace(" ", "%20").replace("Æ", "ae")
        }

        var url = "https://partner.tcgplayer.com/x3/phl.asmx/p?pk=MTGCARDSINFO&s=$setName&p=$cardName"

        LOG.d("loading price for card $cardName")
        try {
            res = doNetworkRequest(url)
        } catch (e: Exception) {
            LOG.e(e)
        }

        if (res != null && (res.lowprice == null || res.lowprice!!.equals("0", ignoreCase = true)) && setName != null && setName.isNotEmpty()) {
            url = "https://partner.tcgplayer.com/x3/phl.asmx/p?pk=MTGCARDSINFO&s=&p=$cardName"
            LOG.d("try again without set for card $cardName")
            try {
                res = doNetworkRequest(url)
            } catch (e: Exception) {
                LOG.e(e)
            }
        }

        if (res == null) {
            res = TCGPrice()
            res.setError(applicationContext.getString(R.string.price_error))
        }

        val intentRes = Intent(idRequest)
        intentRes.putExtra(REST_RESULT, res)
        intentRes.putExtra(REST_URL, url)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentRes)
    }

    @Throws(Exception::class)
    private fun doNetworkRequest(url: String): TCGPrice {
        val tcgPrice = TCGPrice()
        val uri = URL(url)
        val urlConnection = uri.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.setRequestProperty("Connection", "close")

        if (urlConnection.responseCode == 200) {
            val inputStream = urlConnection.inputStream

            val xmlFactoryObject = XmlPullParserFactory.newInstance()
            val myparser = xmlFactoryObject.newPullParser()
            myparser.setInput(inputStream, null)
            var event = myparser.eventType
            var isHiPrice = false
            var isLowPrice = false
            var isAvgPrice = false
            var isLink = false
            while (event != XmlPullParser.END_DOCUMENT) {
                val name = myparser.name
                if (event == XmlPullParser.START_TAG) {
                    isHiPrice = false
                    isLowPrice = false
                    isAvgPrice = false
                    isLink = false
                    if (name == "hiprice") {
                        isHiPrice = true
                    }
                    if (name == "avgprice") {
                        isAvgPrice = true
                    }
                    if (name == "lowprice") {
                        isLowPrice = true
                    }
                    if (name == "link") {
                        isLink = true
                    }
                } else if (event == XmlPullParser.TEXT) {
                    if (isHiPrice) {
                        tcgPrice.hiPrice = myparser.text
                    }
                    if (isLowPrice) {
                        tcgPrice.lowprice = myparser.text
                    }
                    if (isAvgPrice) {
                        tcgPrice.avgPrice = myparser.text
                    }
                    if (isLink) {
                        tcgPrice.link = myparser.text
                    }
                } else if (event == XmlPullParser.END_TAG) {
                    isHiPrice = false
                    isLowPrice = false
                    isAvgPrice = false
                    isLink = false
                }
                event = myparser.next()
            }
            if (tcgPrice.hiPrice == null) {
                tcgPrice.setError(applicationContext.getString(R.string.price_error))
            }
            inputStream.close()
        } else {
            if (urlConnection.responseCode == 500) {
                tcgPrice.isNotFound = true
            }
            tcgPrice.setError(applicationContext.getString(R.string.price_error))
        }
        urlConnection.disconnect()
        return tcgPrice
    }

    companion object {
        const val EXTRA_PARAMS = "NetworkIntentService.EXTRA_PARAMS"
        const val EXTRA_ID = "NetworkIntentService.EXTRA_ID"
        const val EXTRA_CARD_NAME = "NetworkIntentService.EXTRA_CARD_NAME"
        const val EXTRA_SET_NAME = "NetworkIntentService.EXTRA_SET_NAME"
        const val REST_RESULT = "com.dbottillo.mtgsearch.network.REST_RESULT"
        const val REST_URL = "com.dbottillo.mtgsearch.network.REST_URL"
    }
}
