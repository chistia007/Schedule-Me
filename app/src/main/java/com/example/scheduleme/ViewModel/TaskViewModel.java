package com.example.scheduleme.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.scheduleme.Service.Model.Task;
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


    //Insert Task
    public long insert_data(String selectTable,String taskTitle,String taskDescription,String dueDate){
        return taskRepository.insert_data(selectTable, taskTitle, taskDescription, dueDate);
    }
    public Boolean updateDatabase(String selectTable, long _id, String title, String description, String dueDate,int corrTabID,String corrTabName, boolean navigation_clicked) {
        return taskRepository.updateDatabase(selectTable,_id,title,description,dueDate,corrTabID,corrTabName,navigation_clicked);
    }

    public Boolean deleteData(int id1, String title, String tableDesc, String dueDate, int id2, String corrTableName, boolean b) {
        return taskRepository.deleteData(id1,title,tableDesc,dueDate,id2,corrTableName,b);
    }
    public void DeleteFromDoneTaskTable(long id){
        taskRepository.DeleteFromDoneTaskTable(id);
    }

    public int getLastAutoGeneratedKey(String selectedTAble) {
        return taskRepository.getLastAutoGeneratedKey(selectedTAble);
    }
}

