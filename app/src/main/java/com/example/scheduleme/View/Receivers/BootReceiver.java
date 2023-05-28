package com.example.scheduleme.View.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.scheduleme.View.UI.SettingsActivity;


public class BootReceiver extends BroadcastReceiver {
    SettingsActivity settingsActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("gg cg", "onReceive: 234");
        settingsActivity=new SettingsActivity();
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("gg cg", "onReceive: 234");
            SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            Boolean  alwaysOnNotificationValue= prefs.getBoolean("alwaysOnNotificationValue", false);
            if(alwaysOnNotificationValue){
                Intent intent1 = new Intent(context, OnGoingNotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);
            }


            int progress = prefs.getInt("progressPref", 255);
            settingsActivity.setTransparency(progress);
        }
    }
}
