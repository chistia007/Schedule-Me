package com.example.scheduleme.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.scheduleme.R;
import com.example.scheduleme.Service.Databse.TaskDatabase;
import com.example.scheduleme.Service.Model.Adapter.TaskAdapter;
import com.example.scheduleme.Service.Model.Interface.OnItemClickListener;
import com.example.scheduleme.Service.Model.UI.Task;
import com.example.scheduleme.ViewModel.TaskViewModel;
import com.example.scheduleme.databinding.ActivityScheduleMeBinding;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleMeActivity extends AppCompatActivity {
    ActivityScheduleMeBinding binding;
    private TaskDatabase taskDatabase;
    private TaskViewModel taskViewModel;


    private List<Task> tasks = new ArrayList<>();
    private List<Task> allTasks = new ArrayList<>();
    private List<Task> houseTasks = new ArrayList<>();
    private List<Task> officeTasks = new ArrayList<>();
    private List<Task> learningTasks = new ArrayList<>();
    private List<Task> extraCurrTasks = new ArrayList<>();
    private List<Task> doneTasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private String taskTitle;
    private String taskDescription;
    private String dueDateBroadcast;

    EditText titleEditText;
    EditText descriptionEditText;
    EditText dueDateEditText;
    String dueDate;
    AutoCompleteTextView dropDowns;
    AlarmManager alarmManager;


    Task task;
    Cursor cursor;
    boolean i;
    long k;
    Boolean checked = false;
    OnItemClickListener listener;
    RecyclerView taskList;
    LinearLayoutManager layoutManager;
    public String selectTable = "allTasks";
    Boolean navigation_clicked = true;
    Calendar calendar;
    public static final String ACTION_DATA_UPDATED = "com.example.fcm.ACTION_DATA_UPDATED";
    Boolean doneChecked = false;
    long line0;
    int line4;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityScheduleMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        taskDatabase= TaskDatabase.getInstance(this);
        //taskDatabase.getWritableDatabase();
        binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDatabase.insert_data("learning","newme1","ddd",null);

            }
        });

        navigationDrawer();



        taskViewModel=new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                allTasks=results;
                Log.d("111---11111111--112222222333333", "updateUI: "+ results);
                updateUI(allTasks);
            }
        });

        taskViewModel.getOfficeTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                officeTasks=results;
            }
        });

        taskViewModel.getHouseTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                houseTasks=results;
            }
        });

        taskViewModel.getLearningTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                learningTasks=results;
            }
        });

        taskViewModel.getExtraCurrTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                extraCurrTasks=results;
            }
        });

        taskViewModel.getDoneTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                doneTasks=results;
            }
        });

    }



    /**Responsible for handling navigation drawer */
    private void navigationDrawer() {
        DrawerLayout drawerLayout = findViewById(com.example.scheduleme.R.id.drawer_layout);
        NavigationView navigationView = findViewById(com.example.scheduleme.R.id.nav_View);

        // Set up ActionBarDrawerToggle and add it to DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                ScheduleMeActivity.this,
                drawerLayout,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        final int officeWorksId = R.id.officeWorks;

        // Set up NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            doneChecked = false;

            if(item.getItemId()==R.id.officeWorks) {
                navigation_clicked = false;
                binding.bucketName.setText("OFFICE");
                selectTable = "office";
                startActivity(new Intent(ScheduleMeActivity.this, SettingsActivity.class));
                updateUI(officeTasks);
            }
            else if(item.getItemId()==R.id.houseWork) {

                navigation_clicked = false;
                binding.bucketName.setText("HOUSE");
                selectTable = "house";
                updateUI(houseTasks);
            }
            else if(item.getItemId()==R.id.learning) {
                navigation_clicked = false;
                binding.bucketName.setText("LEARNING");
                selectTable = "learning";
                updateUI(learningTasks);
            }
            else if(item.getItemId()==R.id.extra_curr) {
                navigation_clicked = false;
                binding.bucketName.setText("EXTRA CURRICULUM");
                selectTable = "extra";
                updateUI(extraCurrTasks);
            }
            else if(item.getItemId()==R.id.done_tasks) {
                navigation_clicked = false;
                doneChecked = true;
                binding.bucketName.setText("DONE TASKS");
                selectTable = "doneTasks";
                 updateUI(doneTasks);
            }
            else if(item.getItemId()==R.id.settings) {
                startActivity(new Intent(ScheduleMeActivity.this, SettingsActivity.class));
                Toast.makeText(this, "asda", Toast.LENGTH_SHORT).show();
            }
            else {
                navigation_clicked = true;
                binding.bucketName.setText("ALL TASKS");
                updateUI(allTasks);
            }


            // Close the navigation drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });

        // Set up AppBar click event to open the navigation drawer
        binding.imageMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));


    }
    /**Responsible for updating ui with selected table from navigation drawer. by default: allTasks table*/
    private void updateUI(List<Task> observedTableData) {
        tasks=observedTableData;
        taskList = findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(tasks, listener);
        taskList.setAdapter(taskAdapter);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        taskList.setLayoutManager(layoutManager);
        taskAdapter.notifyDataSetChanged();

        // Updating the widget whenever adding a new TODOs
//        Context context = getApplicationContext();
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        ComponentName componentName = new ComponentName(context, My_Widget.class);
//        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
//        My_Widget m = new My_Widget();
//        m.onUpdate(context, appWidgetManager, appWidgetIds);

    }
    /**Responsible for adding new tasks*/
//    private void showAddTaskDialog() {
//        // Create an AlertDialog builder and set the title and message
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
//        builder.setTitle(R.string.add_task_dialog_title);
//        builder.setMessage(R.string.add_task_dialog_message);
//
//        // Set the message text color
//
//
//        // Inflate the layout for the dialog and get the EditText views
//        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
//        titleEditText = view.findViewById(R.id.edit_text_title);
//        descriptionEditText = view.findViewById(R.id.edit_text_description);
//        dueDateEditText = view.findViewById(R.id.edit_text_due_date);
//        dropDowns = view.findViewById(R.id.dropDownText);
//
//        dropDownMenu();
//
//        // Set the layout for the AlertDialog and set the positive and negative buttons
//        builder.setView(view);
//        builder.setPositiveButton(R.string.add_task_dialog_add_button, new DialogInterface.OnClickListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//
//                // Show a Toast message to confirm that the task was added
//                Toast.makeText(ScheduleMeActivity.this, R.string.task_added_message, Toast.LENGTH_SHORT).show();
//
//                taskTitle = titleEditText.getText().toString();
//                taskDescription = descriptionEditText.getText().toString();
//                dueDate = dueDateEditText.getText().toString();
//                selectTable = dropDowns.getText().toString();
//                if (selectTable.equals("")) {
//                    selectTable = "allTasks";
//                }
//
//                k = TaskDatabase.insert_data(selectTable, taskTitle, taskDescription, dueDate);
//                if (k != 0) {
//                    Toast.makeText(ScheduleMeActivity.this, "Successfully data Inserted", Toast.LENGTH_SHORT).show();
//                    updateUI(selectTable);
//                    binding.bucketName.setText("ALL TASKS");
//                } else {
//                    Toast.makeText(ScheduleMeActivity.this, "Insertion failed to", Toast.LENGTH_SHORT).show();
//                }
//
//                //setting reminder and sending the selected table so that we can delete it from the notification bar when we receive broadcast
//                if (!dueDate.equals("")) {
//                    try {
//                        setReminder(selectTable);
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//
//            }
//        });
//        builder.setNegativeButton(R.string.add_task_dialog_cancel_button, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        // Set up the due date EditText view to show a date and time picker dialog
//        dueDateEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get the current date and time
//                calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//                int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                int minute = calendar.get(Calendar.MINUTE);
//
//                // Show a date and time picker dialog
//                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleMeActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                                // When the user sets the date, show a time picker dialog
//                                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleMeActivity.this,
//                                        new TimePickerDialog.OnTimeSetListener() {
//                                            @RequiresApi(api = Build.VERSION_CODES.S)
//                                            @Override
//                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                                // When the user sets the time, update the due date EditText view
//                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
//                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));
//
//                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                                                // Create a new Intent to trigger the alarm
//                                                taskTitle = titleEditText.getText().toString();
//                                                taskDescription = descriptionEditText.getText().toString();
//                                                dueDateBroadcast = dateFormat.format(calendar.getTime());
//
//
//                                            }
//                                        }, hour, minute, false);
//                                timePickerDialog.show();
//                            }
//                        }, year, month, day);
//                datePickerDialog.show();
//                taskAdapter.notifyDataSetChanged();
//            }
//        });
//
//        // Show the AlertDialog
//        AlertDialog dialog = builder.create();
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.show();
//    }
//    /**Responsible for creating bucket shown in navigation drawer*/
//    private void dropDownMenu() {
//        String items[] = new String[]{
//                "allTasks",
//                "officeWork",
//                "houseWork",
//                "learning",
//                "extraCurr"
//        };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                ScheduleMeActivity.this,
//                R.layout.drop_down_text,
//                items
//        );
//
//        dropDowns.setAdapter(adapter);
//    }
    /**Responsible for setting reminder from showAddTaskDialog() and updateTaskDialog()*/
//    private void setReminder(String selectTable) throws ParseException {
//        Intent intent = new Intent(ScheduleMeActivity.this, AlarmReceiver.class);
//        intent.putExtra("taskTitle", taskTitle);
//        intent.putExtra("taskDescription", taskDescription);
//        intent.putExtra("tableName", selectTable);
//        intent.putExtra("dueDate", dueDateBroadcast);
//        int id1 = taskDatabase.getLastAutoGeneratedKey("allTasks")-1;
//        int id2 = taskDatabase.getLastAutoGeneratedKey(selectTable)-1;
//        Log.d("123123", "setReminder: 123123$"+id1+ "@"+id2);
//        intent.putExtra("id1", id1);
//        intent.putExtra("id2", id2);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(ScheduleMeActivity.this, id1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
//
//        // Set the alarm
//        if(calendar!=null) {
//
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        }
//        else{
//            if (dueDate!="") {
//                if (selectTable=="allTasks"){
//                    intent.putExtra("id1", line0);
//                    intent.putExtra("id2", line4);
//                    pendingIntent = PendingIntent.getBroadcast(ScheduleMeActivity.this, (int)line0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
//                }
//                else {
//                    intent.putExtra("id1", line4);
//                    intent.putExtra("id2", line0);
//                    pendingIntent = PendingIntent.getBroadcast(ScheduleMeActivity.this, line4, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
//                }
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                Date date = dateFormat.parse(dueDate);
//                calendar = Calendar.getInstance();
//                calendar.setTime(date);
//                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//            }
//
//        }
//
//
//    }
}