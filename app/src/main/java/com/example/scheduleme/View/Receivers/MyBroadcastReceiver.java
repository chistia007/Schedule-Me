package com.example.scheduleme.View.Receivers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.scheduleme.Service.Databse.TaskDatabase;
import com.example.scheduleme.ViewModel.TaskViewModel;

public class MyBroadcastReceiver extends BroadcastReceiver {
        TaskViewModel taskViewModel;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Initialize the TaskViewModel
        taskViewModel = TaskViewModel.getInstance();

        String action = intent.getAction();

        if (action != null && action.equals("ACTION_DONE")) {
            String tableName = intent.getStringExtra("tableName");
            String title = intent.getStringExtra("taskTitle");
            String tableDesc = intent.getStringExtra("tableDescription");
            String dueDate = intent.getStringExtra("dueDate");
            int id1 = intent.getIntExtra("id1",-1);
            int id2 = intent.getIntExtra("id2",-1);

            Log.d("fourth", "onReceive: received"+tableName);
            taskViewModel.deleteData(id1,title,tableDesc,dueDate,id2,tableName,true);
        }
    }
}

