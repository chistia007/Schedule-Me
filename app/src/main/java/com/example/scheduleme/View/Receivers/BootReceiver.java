package com.example.scheduleme.View.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.scheduleme.R;
import com.example.scheduleme.View.UI.SettingsActivity;
import com.example.scheduleme.View.Widget.My_Widget1;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("rrr", "onReceive: rrr");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            Boolean  alwaysOnNotificationValue= prefs.getBoolean("alwaysOnNotificationValue", false);
            if(alwaysOnNotificationValue){
                Intent intent1 = new Intent(context, OnGoingNotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);
            }

            //set widget transparency
            int progress = prefs.getInt("progressPref", 255);
            String packageName= context.getPackageName();
            RemoteViews remoteViews = new RemoteViews(packageName, R.layout.my__widget1);
            int backgroundColor = Color.argb(progress, 158, 144, 144);
            remoteViews.setInt(R.id.widgetLayout, "setBackgroundColor", backgroundColor);
            // Update the widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, My_Widget1.class);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }
}