package com.example.scheduleme.ViewModel;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.scheduleme.Service.Model.UI.Task;
import com.example.scheduleme.Service.Repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private  TaskRepository taskRepository;
    private  static TaskViewModel taskViewModel;
    private Task task;
    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository=TaskRepository.getInstance(application);

    }
    public static TaskViewModel getInstance(){
        if(taskViewModel==null){
            taskViewModel=new TaskViewModel(new Application());
        }
        return taskViewModel;
    }

    public MutableLiveData<List<Task>> getAllTasksLiveData() {
        return taskRepository.getAllTasksLiveData();
    }
    public MutableLiveData<List<Task>> getOfficeTasksLiveData() {
        return taskRepository.getOfficeTasksLiveData();
    }
    public MutableLiveData<List<Task>> getHouseTasksLiveData() {
        return taskRepository.getHouseTasksLiveData();
    }
    public MutableLiveData<List<Task>> getLearningTasksLiveData() {
        return taskRepository.getLearningTasksLiveData();
    }
    public MutableLiveData<List<Task>> getExtraCurrTasksLiveData() {
        return taskRepository.getExtraCurrTasksLiveData();
    }
    public MutableLiveData<List<Task>> getDoneTasksLiveData() {
        return taskRepository.getDoneTasksLiveData();
    }

}

