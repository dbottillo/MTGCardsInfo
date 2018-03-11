package com.dbottillo.mtgsearchfree.model.network;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.TCGPrice;
import com.dbottillo.mtgsearchfree.util.LOG;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkIntentService extends IntentService {
    public static final String EXTRA_PARAMS = "NetworkIntentService.EXTRA_PARAMS";
    public static final String EXTRA_ID = "NetworkIntentService.EXTRA_ID";
    public static final String EXTRA_CARD_NAME = "NetworkIntentService.EXTRA_CARD_NAME";
    public static final String EXTRA_SET_NAME = "NetworkIntentService.EXTRA_SET_NAME";
    public static final String REST_RESULT = "com.dbottillo.mtgsearch.network.REST_RESULT";
    public static final String REST_URL = "com.dbottillo.mtgsearch.network.REST_URL";

    public NetworkIntentService() {
        super("NetworkIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TCGPrice res = null;
        Bundle extras = intent.getExtras();
        Bundle params = extras.getParcelable(EXTRA_PARAMS);
        String cardName = "";
        String setName = "";
        String idRequest = "";

        if (params != null) {
            cardName = params.getString(EXTRA_CARD_NAME, "");
            setName = params.getString(EXTRA_SET_NAME, "").replace(" ", "%20");
            idRequest = params.getString(EXTRA_ID);
            cardName = cardName.replace(" ", "%20").replace("Æ", "ae");
        }

        String url = "http://partner.tcgplayer.com/x3/phl.asmx/p?pk=MTGCARDSINFO&s=" + setName + "&p=" + cardName;

        LOG.INSTANCE.d("loading price for card " + cardName);
        try {
            res = doNetworkRequest(url);
        } catch (Exception e) {
            LOG.INSTANCE.e(e);
        }

        if (res != null && (res.getLowprice() == null || res.getLowprice().equalsIgnoreCase("0")) && setName != null && setName.length() > 0) {
            url = "http://partner.tcgplayer.com/x3/phl.asmx/p?pk=MTGCARDSINFO&s=&p=" + cardName;
            LOG.INSTANCE.d("try again without set for card " + cardName);
            try {
                res = doNetworkRequest(url);
            } catch (Exception e) {
                LOG.INSTANCE.e(e);
            }
        }

        if (res == null) {
            res = new TCGPrice();
            res.setError(getApplicationContext().getString(R.string.price_error));
        }

        Intent intentRes = new Intent(idRequest);
        intentRes.putExtra(REST_RESULT, res);
        intentRes.putExtra(REST_URL, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentRes);
    }

    private TCGPrice doNetworkRequest(String url) throws Exception {
        TCGPrice tcgPrice = new TCGPrice();
        URL uri = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Connection", "close");

        if (urlConnection.getResponseCode() == 200) {
            InputStream in = urlConnection.getInputStream();

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setInput(in, null);
            int event = myparser.getEventType();
            boolean isHiPrice = false;
            boolean isLowPrice = false;
            boolean isAvgPrice = false;
            boolean isLink = false;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myparser.getName();
                if (event == XmlPullParser.START_TAG) {
                    isHiPrice = false;
                    isLowPrice = false;
                    isAvgPrice = false;
                    isLink = false;
                    if (name.equals("hiprice")) {
                        isHiPrice = true;
                    }
                    if (name.equals("avgprice")) {
                        isAvgPrice = true;
                    }
                    if (name.equals("lowprice")) {
                        isLowPrice = true;
                    }
                    if (name.equals("link")) {
                        isLink = true;
                    }

                } else if (event == XmlPullParser.TEXT) {
                    if (isHiPrice) {
                        tcgPrice.setHiPrice(myparser.getText());
                    }
                    if (isLowPrice) {
                        tcgPrice.setLowprice(myparser.getText());
                    }
                    if (isAvgPrice) {
                        tcgPrice.setAvgPrice(myparser.getText());
                    }
                    if (isLink) {
                        tcgPrice.setLink(myparser.getText());
                    }

                } else if (event == XmlPullParser.END_TAG) {
                    isHiPrice = false;
                    isLowPrice = false;
                    isAvgPrice = false;
                    isLink = false;

                }
                event = myparser.next();
            }
            if (tcgPrice.getHiPrice() == null) {
                tcgPrice.setError(getApplicationContext().getString(R.string.price_error));
            }
            in.close();
        } else {
            if (urlConnection.getResponseCode() == 500) {
                tcgPrice.setNotFound(true);
            }
            tcgPrice.setError(getApplicationContext().getString(R.string.price_error));
        }
        urlConnection.disconnect();
        return tcgPrice;
    }
}
