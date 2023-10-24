package com.kinghorn.justtasks;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class TaskUtils {
    public static String GetDateSuffix( int day) {
        switch (day) {
            case 1: case 21: case 31:
                return ("st");

            case 2: case 22:
                return ("nd");

            case 3: case 23:
                return ("rd");

            default:
                return ("th");
        }
    }

    public static int ConvertDPtoPixel(int dp, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    public static boolean HasChanges(JSONObject original, JSONObject updated) {
        boolean _has_changes = false;
        try {
            Iterator<String> keys = updated.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (original.has(key)) {
                    Log.d("JT", original.toString());
                    Log.d("JT", updated.toString());
                    _has_changes = !original.get(key).equals(updated.get(key));
                    Log.d("JT", String.valueOf(_has_changes));
                } else {
                    _has_changes = true;
                }
            }
        } catch(JSONException e) {
            _has_changes = false;
        }

        return _has_changes;
    }
}
