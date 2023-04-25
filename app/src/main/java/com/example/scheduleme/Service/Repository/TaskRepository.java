package com.example.scheduleme.Service.Repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.scheduleme.Service.Databse.TaskDatabase;
import com.example.scheduleme.Service.Model.Task;

import java.util.List;

public class TaskRepository {

    private MutableLiveData<List<Task>> tasksLiveData;
    private static TaskRepository taskRepository;
    private List<Task> mTasks;
    TaskDatabase taskDatabase;
    Context context;


    private TaskRepository(Context context) {
        this.context=context;


    }

    public static synchronized TaskRepository getInstance(Context context) {
        if (taskRepository == null) {
            taskRepository = new TaskRepository(context);
        }
        return taskRepository;
    }



    public MutableLiveData<List<Task>> getTasksLiveData() {
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getTasksLiveData();
    }


}
