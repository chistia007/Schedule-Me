package com.example.scheduleme.View.Receivers;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import com.example.scheduleme.R;
import com.example.scheduleme.Service.Databse.TaskDatabase;
import com.example.scheduleme.ViewModel.TaskViewModel;


public class OnGoingNotificationReceiver extends BroadcastReceiver {
    String title;
    TaskViewModel taskViewModel;
    Cursor cursor;
    String currentDate1;
    String nextDate;
    Date currentDate;
    int index;
    TaskDatabase taskDatabase;
    @SuppressLint("Range")

    public void onReceive(Context context, Intent intent) {
        ArrayList<String> dates = new ArrayList<>();
        //taskViewModel=TaskViewModel.getInstance();
        taskDatabase=TaskDatabase.getInstance(context);

        ///fixed notification
        //TODO: fetch from viewModel
        cursor = taskDatabase.getInfo("allTasks");
        while (cursor.moveToNext()) {
            if (!cursor.getString(3).equals("")) { //since due date at 4th index
                dates.add(cursor.getString(3));
            }
        }
        Collections.sort(dates);
        currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        currentDate1 = dateFormat.format(currentDate);

        // Find the index of the next upcoming date
        index = -1;
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).compareTo(currentDate1) > 0) {
                index = i;
                break;
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel for Android Oreo and higher
            NotificationChannel channel = new NotificationChannel("channel_id", "CHANNEL_NAME", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("CHANNEL_DESCRIPTION");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setSound(null, null);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }

        if (index != -1) {
            nextDate = dates.get(index);
            //finding title and date based on date since we sorted date without keeping track of other info.
            String[] columns = {"title"};
            String selection = "dueDate = ?";
            String[] selectionArgs = {nextDate};

            //TODO: fetch from viewModel
            Cursor cursor = taskDatabase.dateBasedQuery("allTasks", columns, selection, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    // do something with the title
                } while (cursor.moveToNext());
            }

            String message = "Next Task : " + title + "\nScheduled on : " + nextDate;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                    .setContentTitle("Upcoming Date")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_add_tasks)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true);


            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(10000000, builder.build());
        }
        else {
            // No more upcoming dates, cancel the notification
            notificationManager.cancel(10000000);

        }

    }
}