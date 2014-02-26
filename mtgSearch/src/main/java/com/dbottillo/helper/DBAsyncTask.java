package com.dbottillo.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dbottillo.mtgsearch.R;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.MTGSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class DBAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {

    public interface DBAsyncTaskListener{
        public void onTaskFinished(ArrayList<?> objects);
        public void onTaskEndWithError(String error);
    }

    private boolean error = false;
    private String errorMessage;
    private Context context;
    private String packageName;

    private DBAsyncTaskListener listener;

    public static final int TASK_SET_LIST = 0;
    public static final int TASK_SINGLE_SET = 1;

    private int type;

    public DBAsyncTask(Context context, DBAsyncTaskListener listener, int type){
        this.context = context;
        this.listener = listener;
        this.type = type;
    }

    public DBAsyncTask setPackageName(String packageName){
        this.packageName = packageName;
        return this;
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        int toLoad = -1;
        if (type == TASK_SET_LIST){
            toLoad = R.raw.set_list;
        }else{
            toLoad = context.getResources().getIdentifier(params[0].toLowerCase()+"_x", "raw", packageName);
        }

        String jsonString = loadFile(toLoad);

        try{
            if (type == TASK_SET_LIST){
                JSONArray json = new JSONArray(jsonString);
                for (int i=json.length()-1; i>=0; i--){
                    JSONObject setJ = json.getJSONObject(i);
                    result.add(MTGSet.createMagicSetFromJson(i, setJ));
                }
            }else{
                JSONObject json = new JSONObject(jsonString);
                JSONArray cards = json.getJSONArray("cards");
                for (int i=0; i<cards.length(); i++){
                    JSONObject cardJ = cards.getJSONObject(i);
                    result.add(MTGCard.createCardFromJson(i, cardJ));
                }
            }
        } catch (JSONException e) {
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        }

        if (type == TASK_SINGLE_SET) {
            Collections.sort(result, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    MTGCard card = (MTGCard) o1;
                    MTGCard card2 = (MTGCard) o2;
                    return card.compareTo(card2);
                }
            });
        }

        return result;
    }

    public class MTGCardComparator implements Comparator<MTGCard> {
        @Override
        public int compare(MTGCard o1, MTGCard o2) {
            return 0;
        }
    }

    private String loadFile(int file){
        InputStream is = context.getResources().openRawResource(file);

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        } catch (IOException e) {
            error = true;
            errorMessage = e.getLocalizedMessage();
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                error = true;
                errorMessage = e.getLocalizedMessage();
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

    @Override
    protected void onPostExecute(ArrayList<Object> result) {
        if (error) {
            listener.onTaskEndWithError(errorMessage);
        }else{
            listener.onTaskFinished(result);
        }
    }

}
