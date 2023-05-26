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
        taskViewModel = new ViewModelProvider.AndroidViewModelFactory((Application) context.getApplicationContext()).create(TaskViewModel.class);

        Log.d("Firestone", "onReceive: received");
        String action = intent.getAction();
        Log.d("second", "onReceive: received");

        if (action != null && action.equals("ACTION_DONE")) {

            Log.d("tjird", "onReceive: received");

            String tableName = intent.getStringExtra("tableName");
            String title = intent.getStringExtra("taskTitle");
            String tableDesc = intent.getStringExtra("tableDescription");
            String dueDate = intent.getStringExtra("dueDate");
            int id1 = intent.getIntExtra("id1",-1);
            int id2 = intent.getIntExtra("id2",-1);

            Log.d("fourth", "onReceive: received"+tableName);
            taskViewModel.deleteData(id1,title,tableDesc,dueDate,id2,tableName,true);
//                SQLiteDatabase db = mDatabase.getWritableDatabase();
//                int rowsDeleted = db.delete("allTasks", "_id=?", new String[]{String.valueOf(id1)});
//                if (rowsDeleted > 0) {
//                    Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
//                }
//                mDatabase.close();
        }
    }
}

