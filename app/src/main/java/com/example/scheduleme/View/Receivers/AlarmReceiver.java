package com.example.scheduleme.View.Receivers;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import com.example.scheduleme.R;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.scheduleme.View.UI.ScheduleMeActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "com.example.fcm";
    private static final String CHANNEL_NAME = "Task Reminder";
    private static final String CHANNEL_DESCRIPTION = "Reminds the user to complete a task.";
    private String taskTitle;
    String tableName;
    String taskDescription;
    String dueDate;
    int id1;
    int id2;
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the task information from the intent
        taskTitle = intent.getStringExtra("taskTitle");
        taskDescription = intent.getStringExtra("taskDescription");
        tableName= intent.getStringExtra("tableName");
        dueDate= intent.getStringExtra("dueDate");
        id1=intent.getIntExtra("id1",-1);
        id2=intent.getIntExtra("id2",-1);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel for Android Oreo and higher
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification and show it
        Intent visitButtonIntent = new Intent(context, ScheduleMeActivity.class);
        visitButtonIntent.setAction("ACTION_VISIT");

        // Create the PendingIntent for the visit button
        PendingIntent visitPendingIntent = PendingIntent.getActivity(context, 0, visitButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Create the visit button and add it to the notification
        NotificationCompat.Action visitAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_name, "Visit", visitPendingIntent).build();

        // Create the intent for the Done button
        Intent doneButtonIntent = new Intent(context, MyBroadcastReceiver.class);
        doneButtonIntent.putExtra("tableName", tableName);
        doneButtonIntent.putExtra("id1", id1);
        doneButtonIntent.putExtra("id2", id2);
        doneButtonIntent.putExtra("taskTitle", taskTitle);
        doneButtonIntent.putExtra("taskDescription", taskDescription);
        doneButtonIntent.putExtra("dueDate", dueDate);
        doneButtonIntent.setAction("ACTION_DONE");
        // Create the PendingIntent for the Done button
        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context, 0, doneButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Create the Done button and add it to the notification
        NotificationCompat.Action doneAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_name, "Done", donePendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_tasks)
                .setContentTitle(taskTitle)
                .setContentText(taskDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .addAction(visitAction)
                .addAction(doneAction)
                .setAutoCancel(true);

        notificationManager.notify(id1, builder.build());
    }
}


