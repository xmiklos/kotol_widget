package com.xmiklos.kotol;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.JobIntentService;

import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 */
public class KotolWidget extends AppWidgetProvider {

    public static final String UPDATE_TEMP_ACTION       = "com.xmiklos.kotol.UPDATE_TEMP_ACTION";
    public static final String ACTION_AUTO_UPDATE       = "AUTO_UPDATE";
    private static final String ALARM_BUTTON_CLICKED    = "com.xmiklos.kotol.ALARM_BUTTON_CLICKED";

    public static boolean alarm_on = false;
    public static MediaPlayer mMediaPlayer;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kotol_widget);
        Intent i = new Intent(context, KotolWidget.class);
        i.setAction(UPDATE_TEMP_ACTION);
        views.setOnClickPendingIntent(R.id.temp, PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT) );
        views.setOnClickPendingIntent(R.id.button3, getPendingSelfIntent(context, ALARM_BUTTON_CLICKED));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.i("KotolWidget", "onUpdate");
        Intent intent = new Intent(context, KotolJobIntentService.class);
        KotolJobIntentService.enqueueWork(context, intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i("KotolWidget", "onReceive");
        Log.i("KotolWidget", intent.getAction());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kotol_widget);
        ComponentName watchWidget = new ComponentName(context, KotolWidget.class);

        if (UPDATE_TEMP_ACTION.equals(intent.getAction())) {
            Intent i = new Intent(context, KotolJobIntentService.class);
            KotolJobIntentService.enqueueWork(context, intent);
        } else if(intent.getAction().equals(ACTION_AUTO_UPDATE))
        {
            Log.i("KotolWidget", "ACTION_AUTO_UPDATE");
            Intent i = new Intent(context, KotolJobIntentService.class);
            KotolJobIntentService.enqueueWork(context, intent);

        } if(intent.getAction().equals(ALARM_BUTTON_CLICKED))
        {
            Log.i("KotolWidget", "ALARM_BUTTON_CLICKED");
            alarm_on = !alarm_on;
            Log.i("alarm_on", alarm_on ? "1" : "0");

            String text = alarm_on ? context.getResources().getString(R.string.alarm_on) : context.getResources().getString(R.string.alarm_off);
            views.setTextViewText(R.id.button3, text);

            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                try {
                    mMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            AppWidgetManager.getInstance(context).updateAppWidget(watchWidget, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
        Log.i("KotolWidget", "starting alarm...");

        try {
            Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        } catch(Exception e) {
        }
    }

    @Override
    public void onDisabled(Context context) {
        // stop alarm only if all widgets have been disabled
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        if (appWidgetIds.length == 0) {
            // stop alarm
            AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
            appWidgetAlarm.stopAlarm();
        }
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, KotolWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

