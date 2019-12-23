package com.xmiklos.kotol;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Temp {
    private int temp;
    private Date createdAt;

    public Date getDownloadedAt() {
        return downloadedAt;
    }

    private Date downloadedAt;
    private int entryId;
    private boolean ok;

    public Temp(JSONObject obj) {
        if (obj == null) {
            ok = false;
            return;
        }
        try {
            temp = obj.getInt("field1");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            createdAt = sdf.parse(obj.getString("created_at"));
            entryId = obj.getInt("entry_id");
            downloadedAt = new Date();

            ok = true;
        } catch (JSONException e) {
            e.printStackTrace();
            ok = false;
        } catch (ParseException e) {
            e.printStackTrace();
            ok = false;
        }
    }

    public int getTemp() {
        return temp;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getEntryId() {
        return entryId;
    }

    public boolean isOk() {
        return ok;
    }
}
