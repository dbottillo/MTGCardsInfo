package com.dbottillo.network;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.dbottillo.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;

public class NetworkIntentService extends IntentService {

    public static final String EXTRA_PARAMS = "NetworkIntentService.EXTRA_PARAMS";
    public static final String EXTRA_ID = "NetworkIntentService.EXTRA_ID";
    public static final String EXTRA_CARD_NAME = "NetworkIntentService.EXTRA_CARD_NAME";
    public static final String REST_RESULT = "com.dbottillo.network..REST_RESULT";

    public NetworkIntentService() {
        super("NetworkIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        OkHttpClient client = new OkHttpClient();

        String res;
        Bundle extras = intent.getExtras();
        Bundle params = extras.getParcelable(EXTRA_PARAMS);
        String cardName = params.getString(EXTRA_CARD_NAME);
        String idRequest = params.getString(EXTRA_ID);

        String url = "http://magictcgprices.appspot.com/api/tcgplayer/price.json?cardname=" + cardName;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            JSONArray price = new JSONArray(response.body().string());
            res = price.get(0).toString();
        } catch (Exception e) {
            res = getApplicationContext().getString(R.string.price_error);
        }

        Intent intentRes = new Intent(idRequest);
        intentRes.putExtra(REST_RESULT, res);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentRes);
    }
}
