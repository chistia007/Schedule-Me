package com.example.scheduleme.View.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.scheduleme.R;
import com.example.scheduleme.ViewModel.TaskViewModel;


public class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Cursor cursor;
    TaskViewModel taskViewModel=TaskViewModel.getInstance();
    public MyWidgetFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        // Initialize the cursor
        cursor = null;
    }

    @Override
    public void onDataSetChanged() {
        cursor = taskViewModel.getInfo("allTasks");
    }

    @Override
    public void onDestroy() {
        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @SuppressLint("Range")
    @Override
    public RemoteViews getViewAt(int position) {
        // Move the cursor to the correct position
        cursor.moveToPosition(cursor.getCount() - position - 1); //the cursor in the reverse position of the given position

        // Get the data from the cursor
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));

        // Create a new RemoteViews object for the item view
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_item);


        // Update the views with data
        views.setViewVisibility(R.id.task_checkbox, View.GONE);
        views.setTextViewText(R.id.task_name, title);
        views.setTextViewText(R.id.task_desc, description);
        views.setTextViewText(R.id.due_time, dueDate);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        // Create a loading view for the widget
        return new RemoteViews(context.getPackageName(), R.layout.widget_loading);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @SuppressLint("Range")
    @Override
    public long getItemId(int position) {
        // Return the id of the item at the given position
        if (cursor != null) {
            cursor.moveToPosition(position);
            return cursor.getInt(cursor.getColumnIndex("_id"));
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}