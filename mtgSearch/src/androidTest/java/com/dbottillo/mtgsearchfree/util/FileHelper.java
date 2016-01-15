package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.content.res.Resources;

import com.dbottillo.mtgsearchfree.helper.CreateDBAsyncTask;
import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class FileHelper {

    public static ArrayList<MTGSet> readSetListJSON(Context context) throws JSONException {
        int setList = context.getResources().getIdentifier("set_list", "raw", context.getPackageName());
        String jsonString = loadFile(context, setList);
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<MTGSet> sets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject setJ = jsonArray.getJSONObject(i);
            try {
                loadFile(context, CreateDBAsyncTask.setToLoad(context, setJ.getString("code")));
                MTGSet set = new MTGSet(i);
                set.setName(setJ.getString("name"));
                set.setCode(setJ.getString("code"));
                sets.add(set);
            } catch (Resources.NotFoundException e) {
                LOG.e("e: " + e.getLocalizedMessage());
            }
        }
        return sets;
    }

    private static String loadFile(Context context, int file) throws Resources.NotFoundException {
        InputStream is = context.getResources().openRawResource(file);

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            return null;
        }

        return writer.toString();
    }
}
