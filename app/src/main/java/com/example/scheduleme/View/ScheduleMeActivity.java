package com.example.scheduleme.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.scheduleme.Service.Databse.TaskDatabase;
import com.example.scheduleme.Service.Model.Task;
import com.example.scheduleme.ViewModel.TaskViewModel;
import com.example.scheduleme.databinding.ActivityScheduleMeBinding;

import java.util.List;

public class ScheduleMeActivity extends AppCompatActivity {
    ActivityScheduleMeBinding binding;
    private TaskDatabase taskDatabase;
    private TaskViewModel taskViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityScheduleMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        taskDatabase= TaskDatabase.getInstance(this);
        taskDatabase.getWritableDatabase();

//        binding.btnLive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                taskDatabase.insert_data("allTasks","me1","ddd",null);
//            }
//        });



        taskViewModel=new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                Toast.makeText(ScheduleMeActivity.this, "22221111", Toast.LENGTH_SHORT).show();
                Toast.makeText(ScheduleMeActivity.this, "Data changed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}