package com.xmiklos.kotol;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.JobIntentService;

/**
 * Implementation of App Widget functionality.
 */
public class KotolWidget extends AppWidgetProvider {

    public static final String UPDATE_TEMP_ACTION = "com.xmiklos.kotol.UPDATE_TEMP_ACTION";
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.kotol_widget);
        Intent i = new Intent(context, KotolWidget.class);
        i.setAction(UPDATE_TEMP_ACTION);
        views.setOnClickPendingIntent(R.id.temp, PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT) );

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
        if (UPDATE_TEMP_ACTION.equals(intent.getAction())) {
            Intent i = new Intent(context, KotolJobIntentService.class);
            KotolJobIntentService.enqueueWork(context, intent);
        } else if(intent.getAction().equals(ACTION_AUTO_UPDATE))
        {
            Log.i("KotolWidget", "ACTION_AUTO_UPDATE");
            Intent i = new Intent(context, KotolJobIntentService.class);
            KotolJobIntentService.enqueueWork(context, intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
        Log.i("KotolWidget", "starting alarm...");
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
}

