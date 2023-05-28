package com.example.scheduleme.View.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;
import com.example.scheduleme.R;

import com.example.scheduleme.View.Receivers.OnGoingNotificationReceiver;
import com.example.scheduleme.View.Widget.My_Widget1;
import com.example.scheduleme.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String ALPHA_KEY = "alpha";

    private SeekBar seekBar;
    private float alpha;
    Intent intent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    int newColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        seekBar = findViewById(R.id.seekBar);

        // Restore the alpha value from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        alpha = prefs.getFloat(ALPHA_KEY, 1.0f);
        seekBar.setProgress((int) (alpha * 255));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTransparency(progress);
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt("progressPref", progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        binding.switchOnGoingNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SettingsActivity.this, OnGoingNotificationReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                if (binding.switchOnGoingNotification.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("alwaysOnNotificationValue", true);
                    editor.apply();
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);
                }
                else{
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("alwaysOnNotificationValue", false);
                    editor.apply();
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    NotificationManagerCompat notificationManager= NotificationManagerCompat.from(SettingsActivity.this);
                    notificationManager.cancel(10000000);

                }

            }
        });

        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Boolean  alwaysOnNotificationValue= prefs.getBoolean("alwaysOnNotificationValue", false);

        if (alwaysOnNotificationValue) {
            binding.switchOnGoingNotification.setChecked(true);
        }
        else{
            binding.switchOnGoingNotification.setChecked(false);

        }

    }

    public void setTransparency(int progress){
        // Get the widget's RemoteViews
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.my__widget1);
        float alpha = (float) progress / 255;
        // Set the alpha value of the widget's RemoteViews
        int backgroundColor = Color.argb((int)(alpha*255), 158, 144, 144);
        remoteViews.setInt(R.id.widgetLayout, "setBackgroundColor", backgroundColor);

        // Update the widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SettingsActivity.this);
        ComponentName componentName = new ComponentName(SettingsActivity.this, My_Widget1.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);

//        Context context = getApplicationContext();
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        ComponentName componentName = new ComponentName(SettingsActivity.this, My_Widget1.class);
//        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the alpha value to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putFloat(ALPHA_KEY, alpha);
        editor.apply();
    }

}
