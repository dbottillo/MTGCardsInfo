package com.dbottillo.network;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.dbottillo.R;
import com.dbottillo.helper.LOG;
import com.dbottillo.resources.TCGPrice;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkIntentService extends IntentService {

    public static final String EXTRA_PARAMS = "NetworkIntentService.EXTRA_PARAMS";
    public static final String EXTRA_ID = "NetworkIntentService.EXTRA_ID";
    public static final String EXTRA_CARD_NAME = "NetworkIntentService.EXTRA_CARD_NAME";
    public static final String REST_RESULT = "com.dbottillo.network..REST_RESULT";
    public static final String REST_ERROR = "com.dbottillo.network..REST_ERROR";

    public NetworkIntentService() {
        super("NetworkIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TCGPrice res;
        Bundle extras = intent.getExtras();
        Bundle params = extras.getParcelable(EXTRA_PARAMS);
        String cardName = params.getString(EXTRA_CARD_NAME);
        String idRequest = params.getString(EXTRA_ID);
        String stringError = null;

        String url = "http://partner.tcgplayer.com/x3/phl.asmx/p?pk=MTGCARDSINFO&s=&p=" + cardName;
        try {
            res = doNetworkRequest(url);
        } catch (Exception e) {
            LOG.e("Price Card Error: " + e.getClass() + " - " + e.getLocalizedMessage());
            res = new TCGPrice();
            res.isAnError();
            res.setErrorPrice(getApplicationContext().getString(R.string.price_error));
            stringError = url;
        }

        Intent intentRes = new Intent(idRequest);
        intentRes.putExtra(REST_RESULT, res);
        intentRes.putExtra(REST_ERROR, stringError);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentRes);
    }

    private TCGPrice doNetworkRequest(String url) throws Exception {
        TCGPrice tcgPrice = new TCGPrice();
        URL uri = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
        urlConnection.setRequestMethod("GET");

        if (urlConnection.getResponseCode() == 200) {
            InputStream in = urlConnection.getInputStream();

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setInput(in, null);
            int event = myparser.getEventType();
            boolean isHiPrice = false;
            boolean isLowPrice = false;
            boolean isAvgPrice = false;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myparser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        isHiPrice = false;
                        isLowPrice = false;
                        isAvgPrice = false;
                        if (name.equals("hiprice")) {
                            isHiPrice = true;
                        }
                        if (name.equals("avgprice")) {
                            isAvgPrice = true;
                        }
                        if (name.equals("lowprice")) {
                            isLowPrice = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (isHiPrice) {
                            tcgPrice.setHiPrice(myparser.getText());
                        }
                        if (isLowPrice) {
                            tcgPrice.setLowprice(myparser.getText());
                        }
                        if (isAvgPrice) {
                            tcgPrice.setAvgPrice(myparser.getText());
                        }
                    case XmlPullParser.END_TAG:
                        isHiPrice = false;
                        isLowPrice = false;
                        isAvgPrice = false;

                        break;
                }
                event = myparser.next();
            }
        } else {
            tcgPrice.setErrorPrice(getApplicationContext().getString(R.string.price_error));
            tcgPrice.isAnError();
        }
        return tcgPrice;
    }

    private String getEntityAsString(InputStream responseEntity, String encoding) throws Exception {
        String r = null;
        InputStream istream = null;
        Writer writer = null;
        Reader reader = null;
        try {
            // Stream length could be greater than the response Content-Length,
            // because the stream will unzip content transparently
            reader = new BufferedReader(new InputStreamReader(responseEntity, encoding == null ? "UTF-8" : encoding), 8192);
            writer = new StringWriter();
            int l;
            char[] buf = new char[8192];
            while ((l = reader.read(buf)) != -1) {
                writer.write(buf, 0, l);
            }
            r = writer.toString();
        } finally {
            if (writer != null)
                writer.close();
            if (reader != null)
                reader.close();
            if (istream != null)
                istream.close();
        }
        return r;
    }
}
