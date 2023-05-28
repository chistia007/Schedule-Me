package com.example.scheduleme.View.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.scheduleme.R;
import com.example.scheduleme.View.UI.ScheduleMeActivity;
import com.example.scheduleme.ViewModel.TaskViewModel;

public class My_Widget1 extends AppWidgetProvider {
    TaskViewModel taskViewModel;
    Cursor cursor;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Log.d("TAG", "onUpdate: ramadan1111");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my__widget1);

            // Set the ListView adapter
            Intent intent = new Intent(context, MyWidgetService.class);
            views.setRemoteAdapter(R.id.list_view, intent);

            //taking to the app's home interface(ALL Task) by clicking top of the widget
            Intent intent0 = new Intent(context, ScheduleMeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.totalTodos, pendingIntent);

            //Total number of todos for widget
            taskViewModel=TaskViewModel.getInstance();
            cursor=taskViewModel.getInfo("allTasks");
            int index = 0; // keep track of the current index
            while (cursor.moveToNext()) {
                index++;
            }
            views.setTextViewText(R.id.totalTodos,"Total " +index +" Todos");

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.list_view);
        }
    }
}

