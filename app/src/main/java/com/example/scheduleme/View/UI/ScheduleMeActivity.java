package com.example.scheduleme.View.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.scheduleme.View.Adapter.TaskAdapter;
import com.example.scheduleme.View.Interface.OnItemClickListener;
import com.example.scheduleme.Service.Model.Task;
import com.example.scheduleme.View.Receivers.AlarmReceiver;
import com.example.scheduleme.View.Widget.My_Widget1;
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
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityScheduleMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if notification permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
        }

        /** Listener for updating and marking as done the existing tasks*/
        listenerForCRUD();

        navigationDrawer();

        taskViewModel=new ViewModelProvider(this).get(TaskViewModel.class);
        liveUpdateProviderForAllTables();

        binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });
    }

    private void liveUpdateProviderForAllTables() {
        taskViewModel.getAllTasksLiveData().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> results) {
                allTasks=results;
                //by default: allTasks table
                binding.bucketName.setText("ALL TASKS");
                updateUI(allTasks);
                UpdateWidget();
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

    /** Updating the widget with tasks whenever CRUD is done*/
    private void UpdateWidget() {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, My_Widget1.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        My_Widget1 m = new My_Widget1();
        m.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void listenerForCRUD() {
        try {
            //click and shows dialog for edit
            listener = new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Task task = tasks.get(position);
                    line0 = task.get_id(position);
                    String line1 = task.getTitle();
                    String line2 = task.getDescription();
                    String line3 = task.getDueDate();
                    line4 = task.getCorrespondingTableId();
                    String line5 = task.getCorrespondingTable();
                    Log.d("noooooooooooooooooooooooooooooooooooo1", "onItemClick: ");
                    UpdateTaskInDialog(line0, line1, line2, line3, line4, line5);
                }


                //checkbox operations: done, undo and etc.
                @Override
                public void onCheckboxClick(View view, int position, boolean isChecked) {
                    Task task = tasks.get(position);
                    task.setComplete(isChecked);

                    /** recovering a done task to its previous corresponding table */
                    if (doneChecked.equals(true)) {
                        Log.d("11", "onCheckboxClick: 11");
                        try {
                            if (!isChecked) {
                                Log.d("111122223333444555555", "onCheckboxClick: 22 "+ task.getCorrespondingTable() );
                                k = taskViewModel.insert_data(task.getCorrespondingTable(), task.getTitle(), task.getDescription(), task.getDueDate());
                                if (k != 0) {
                                    Log.d("11", "onCheckboxClick: 33");
                                    Toast.makeText(ScheduleMeActivity.this, "Successfully data Inserted", Toast.LENGTH_SHORT).show();

                                    taskViewModel.DeleteFromDoneTaskTable(task.get_id());
                                } else {
                                    Log.d("11", "onCheckboxClick: 44");
                                    Toast.makeText(ScheduleMeActivity.this, "Insertion failed to", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } finally {

                        }

                    }

                    if (isChecked) {
                        // adding task to done task table and deleting it from regular tables
                        int tabId = Float.valueOf(task.get_id(position)).intValue();
                        Log.d("maliha", "onCheckboxClick: "+  task.getCorrespondingTableId());
                        Boolean s = taskViewModel.deleteData(tabId , task.getTitle(), task.getDescription(), task.getDueDate(), task.getCorrespondingTableId(), task.getCorrespondingTable(), navigation_clicked);
                        if (s) {
                            Toast.makeText(ScheduleMeActivity.this, "Task Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
        } catch (Exception e) {
            Log.e("listener error", "Error occurred while performing database operations: " + e.getMessage());
        }
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
                binding.bucketName.setText("OFFICE WORK");
                selectTable = "office";
                updateUI(officeTasks);
            }
            else if(item.getItemId()==R.id.houseWork) {

                navigation_clicked = false;
                binding.bucketName.setText("HOUSE WORK");
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
    @SuppressLint("NotifyDataSetChanged")
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
    }
    /**Responsible for adding new tasks*/
    private void showAddTaskDialog() {
        // Create an AlertDialog builder and set the title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.add_task_dialog_title);
        builder.setMessage(R.string.add_task_dialog_message);

        // Set the message text color


        // Inflate the layout for the dialog and get the EditText views
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        titleEditText = view.findViewById(R.id.edit_text_title);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        dueDateEditText = view.findViewById(R.id.edit_text_due_date);
        dropDowns = view.findViewById(R.id.dropDownText);

        dropDownMenu();

        // Set the layout for the AlertDialog and set the positive and negative buttons
        builder.setView(view);
        builder.setPositiveButton(R.string.add_task_dialog_add_button, new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskTitle = titleEditText.getText().toString();
                taskDescription = descriptionEditText.getText().toString();
                dueDate = dueDateEditText.getText().toString();
                //bucketing
                selectTable = dropDowns.getText().toString();
                if (selectTable.equals("")) {
                    selectTable = "allTasks";
                }

                k = taskViewModel.insert_data(selectTable, taskTitle, taskDescription, dueDate);
                if (k == 0) {
                    Toast.makeText(ScheduleMeActivity.this, "Insertion failed to", Toast.LENGTH_SHORT).show();
                }
                //setting reminder and sending the selected table so that we can delete it from the notification bar when we receive broadcast
                if (!dueDate.equals("")) {
                    try {
                        setReminder(selectTable);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }


            }
        });
        builder.setNegativeButton(R.string.add_task_dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Set up the due date EditText view to show a date and time picker dialog
        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date and time
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Show a date and time picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleMeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // When the user sets the date, show a time picker dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleMeActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.S)
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // When the user sets the time, update the due date EditText view
                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));

                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                // Create a new Intent to trigger the alarm
                                                taskTitle = titleEditText.getText().toString();
                                                taskDescription = descriptionEditText.getText().toString();
                                                dueDateBroadcast = dateFormat.format(calendar.getTime());


                                            }
                                        }, hour, minute, false);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                datePickerDialog.show();
                taskAdapter.notifyDataSetChanged();
            }
        });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }
    //*Responsible for editing existing tasks */
    private void UpdateTaskInDialog(long id, String line1, String line2, String line3, int line4, String line5) {
        // Create an AlertDialog builder and set the title and message
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.add_task_dialog_title);
        builder.setMessage(R.string.add_task_dialog_message);

        // Inflate the layout for the dialog and get the EditText views
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        titleEditText = view.findViewById(R.id.edit_text_title);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        dueDateEditText = view.findViewById(R.id.edit_text_due_date);
        dropDowns = view.findViewById(R.id.dropDownText);

        dropDownMenu();
        // setting texts to edittext when user click an item on recyclerview
        titleEditText.setText(line1);
        descriptionEditText.setText(line2);
        dueDateEditText.setText(line3);


        // Set the layout for the AlertDialog and set the positive and negative buttons
        builder.setView(view);
        builder.setPositiveButton(R.string.add_task_dialog_add_button, new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskTitle = titleEditText.getText().toString();
                taskDescription = descriptionEditText.getText().toString();
                dueDate = dueDateEditText.getText().toString();
                selectTable = dropDowns.getText().toString();

                if (taskTitle.equals("")) {
                    Toast.makeText(ScheduleMeActivity.this, "You can not title empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (selectTable.equals("")) {
                        selectTable = "allTasks";
                    }
                    dropDownMenu();
                    i = taskViewModel.updateDatabase(selectTable, id, taskTitle, taskDescription, dueDate, line4, line5, navigation_clicked);
                    if (i) {
                        Toast.makeText(ScheduleMeActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ScheduleMeActivity.this, "It doesn't  belong to " + selectTable, Toast.LENGTH_SHORT).show();
                    }
                }
                if (!dueDate.equals("")) {
                    try {
                        setReminder(selectTable);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.add_task_dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Set up the due date EditText view to show a date and time picker dialog
        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date and time
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Show a date and time picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleMeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // When the user sets the date, show a time picker dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleMeActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.S)
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // When the user sets the time, update the due date EditText view
                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));

                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                // Create a new Intent to trigger the alarm
                                                taskTitle = titleEditText.getText().toString();
                                                taskDescription = descriptionEditText.getText().toString();
                                                dueDateBroadcast = dateFormat.format(calendar.getTime());
                                            }
                                        }, hour, minute, false);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Show the AlertDialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**Responsible for creating bucket shown in navigation drawer*/
    private void dropDownMenu() {
        String items[] = new String[]{
                "allTasks",
                "officeWork",
                "houseWork",
                "learning",
                "extraCurr"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ScheduleMeActivity.this,
                R.layout.drop_down_text,
                items
        );

        dropDowns.setAdapter(adapter);
    }
    /**Responsible for setting reminder from showAddTaskDialog() and updateTaskDialog()*/
    private void setReminder(String selectTable) throws ParseException {
        Intent intent = new Intent(ScheduleMeActivity.this, AlarmReceiver.class);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskDescription", taskDescription);
        intent.putExtra("tableName", selectTable);
        intent.putExtra("dueDate", dueDateBroadcast);
        int id1 = taskViewModel.getLastAutoGeneratedKey("allTasks")-1;
        int id2 = taskViewModel.getLastAutoGeneratedKey(selectTable)-1;
        Log.d("123123", "setReminder: 123123$"+id1+ "@"+id2);
        intent.putExtra("id1", id1);
        intent.putExtra("id2", id2);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ScheduleMeActivity.this, id1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Set the alarm
        if(calendar!=null) {

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else{
            /**setting pending intent for alarm with the corresponding task's id of allTasks table so that each reminder have unique pending intent id.
            And thus saving pending intent from replacing  each other*/
            if (dueDate!="") {
                if (selectTable=="allTasks"){
                    intent.putExtra("id1", line0);
                    intent.putExtra("id2", line4);
                    pendingIntent = PendingIntent.getBroadcast(ScheduleMeActivity.this, (int)line0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                }
                else {
                    intent.putExtra("id1", line4);
                    intent.putExtra("id2", line0);
                    pendingIntent = PendingIntent.getBroadcast(ScheduleMeActivity.this, line4, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = dateFormat.parse(dueDate);
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Allow permission to get notified from settings", Toast.LENGTH_LONG).show();
            }
        }
    }
}