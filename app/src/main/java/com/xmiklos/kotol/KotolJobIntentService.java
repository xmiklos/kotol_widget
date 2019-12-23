package com.xmiklos.kotol;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class KotolJobIntentService extends JobIntentService {
    private static final int JOB_ID = 1;
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, KotolJobIntentService.class, JOB_ID, intent);
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Temp t = TempGetter.get();
        Log.i("KotolJobIntentService", "temp " + t.getTemp());
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.kotol_widget);
        if (t.isOk()) {
            views.setTextViewText(R.id.temp, t.getTemp() + "°");
            views.setTextViewText(R.id.update, new SimpleDateFormat("HH:mm", Locale.getDefault()).format(t.getCreatedAt()));
        } else {
            views.setTextViewText(R.id.temp, "...");
            views.setTextViewText(R.id.update, "");
        }

        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();*/


        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(new ComponentName(this, KotolWidget.class), views);
    }
}
