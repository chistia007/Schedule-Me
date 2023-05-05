package com.example.scheduleme.Service.Repository;

import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.MutableLiveData;
import com.example.scheduleme.Service.Databse.TaskDatabase;
import com.example.scheduleme.Service.Model.UI.Task;

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



    public MutableLiveData<List<Task>> getAllTasksLiveData() {
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getAllTasksLiveData();
    }
    public MutableLiveData<List<Task>> getOfficeTasksLiveData() {
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getOfficeTasksLiveData();
    }
    public MutableLiveData<List<Task>> getExtraCurrTasksLiveData() {
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getExtraCurrTasksLiveData();
    }
    public MutableLiveData<List<Task>> getHouseTasksLiveData() {
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getHouseTasksLiveData();
    }
    public MutableLiveData<List<Task>> getLearningTasksLiveData() {
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getLearningTasksLiveData();
    }
    public MutableLiveData<List<Task>> getDoneTasksLiveData() {
        taskDatabase=TaskDatabase.getInstance(context);
        return  taskDatabase.getDoneTasksLiveData();
    }

    public void insert_data(String selectTable,String title,String description, String dueDate){
        taskDatabase.insert_data(selectTable,title,description,dueDate);
    }

    public void updateDatabase(String selectTable, long _id, String title, String description, String dueDate,int corrTabID,String corrTabName, boolean navigation_clicked) {

    }

    public void deleteData(long id, String title, String description, String dueDate,int corrTabID,String corrTabName, boolean navigation_clicked){

    }

    public void getLastAutoGeneratedKey(String tableName) {

    }

    public Cursor getInfo(String selectTable){
        return taskDatabase.getInfo(selectTable);
    }


}
