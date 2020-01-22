package com.xmiklos.kotol;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TempGetter {
    final static String thingspeak_url = "http://api.thingspeak.com/channels/912912/fields/1/last.json?api_key=2EGFUWDSDEV9I2EO";

    static Temp get() {
        String json = "";
        JSONObject data = null;

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL( thingspeak_url );
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
            json = s.hasNext() ? s.next() : "";
            data = new JSONObject( json );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("IOException", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new Temp(data);
    }
}
