package com.example.scheduleme.Service.Repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.scheduleme.Service.Dao.TaskDao;
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
//        if(tasksLiveData==null){
//           tasksLiveData=new MutableLiveData<>();
//        }
//        Log.d("11111111111111111111", "getTasksLiveData: 1111");
//        taskDatabase=TaskDatabase.getInstance(context);
//        mTasks = taskDatabase.getTasks("allTasks");
//        tasksLiveData.postValue(mTasks);
//        return tasksLiveData;
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getTasksLiveData();
    }


}
