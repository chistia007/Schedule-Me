package com.example.scheduleme.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.scheduleme.Service.Model.Task;
import com.example.scheduleme.Service.Repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;
    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository=TaskRepository.getInstance(application);

    }

    public MutableLiveData<List<Task>> getTasksLiveData() {
        return taskRepository.getTasksLiveData();
    }
}

