package com.example.scheduleme.Service.Databse;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.scheduleme.Service.Model.UI.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabase extends SQLiteOpenHelper {
    private static final String dbName="User.tasks";
    SQLiteDatabase db;
    ContentValues c;
    long rowId;
    Cursor cursor;
    int nextKey;
    List<Task> tasks;
    List<Task> allTasks;
    List<Task> houseTasks;
    List<Task> officeTasks;
    List<Task> extraCurrTasks;
    List<Task> learningTasks;
    List<Task> doneTasks;

    private static TaskDatabase taskDatabase;
    private static MutableLiveData<List<Task>> allTasksLiveData = new MutableLiveData<>(new ArrayList<>());
    private static MutableLiveData<List<Task>> officeTasLiveData = new MutableLiveData<>(new ArrayList<>());
    private static MutableLiveData<List<Task>> houseTaskLiveData = new MutableLiveData<>(new ArrayList<>());
    private static MutableLiveData<List<Task>> extraTasksLiveData = new MutableLiveData<>(new ArrayList<>());
    private static MutableLiveData<List<Task>> learningTasksLiveData = new MutableLiveData<>(new ArrayList<>());
    private static MutableLiveData<List<Task>> doneTasksLiveData = new MutableLiveData<>(new ArrayList<>());

    private TaskDatabase(Context context, MutableLiveData<List<Task>> allTasksLiveData,MutableLiveData<List<Task>> officeTasLiveData,
                         MutableLiveData<List<Task>> houseTaskLiveData,MutableLiveData<List<Task>> extraTasksLiveData,
                         MutableLiveData<List<Task>> learningTasksLiveData,MutableLiveData<List<Task>> doneTasksLiveData) {
        super(context, dbName, null, 1);
        this.allTasksLiveData=allTasksLiveData;
        this.officeTasLiveData=officeTasLiveData;
        this.houseTaskLiveData=houseTaskLiveData;
        this.extraTasksLiveData=extraTasksLiveData;
        this.learningTasksLiveData=learningTasksLiveData;
        this.doneTasksLiveData=doneTasksLiveData;

        allTasks= getTasks("allTasks");
        allTasksLiveData.postValue(allTasks);
        officeTasks= getTasks("officeWork");
        officeTasLiveData.postValue(allTasks);
        houseTasks= getTasks("houseWork");
        houseTaskLiveData.postValue(allTasks);
        learningTasks= getTasks("learning");
        learningTasksLiveData.postValue(allTasks);
        extraCurrTasks= getTasks("extraCurr");
        extraTasksLiveData.postValue(allTasks);
        doneTasks= getTasks("doneTasks");
        doneTasksLiveData.postValue(allTasks);
    }

    public static synchronized TaskDatabase getInstance(Context context) {
        if (taskDatabase == null) {
            taskDatabase = new TaskDatabase(context, allTasksLiveData,officeTasLiveData,houseTaskLiveData,extraTasksLiveData,learningTasksLiveData,doneTasksLiveData);
        }
        return taskDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String q1="create table allTasks(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q1);
        String q2="create table officeWork(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q2);
        String q3="create table houseWork(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q3);
        String q4="create table learning(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q4);
        String q5="create table extraCurr(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q5);
        String q6="create table doneTasks(_id integer primary key autoincrement, title text, description text, dueDate text,correspondingTable Text)";
        sqLiteDatabase.execSQL(q6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // will drop the database if already exists
        sqLiteDatabase.execSQL("drop table if exists tasks");
        onCreate(sqLiteDatabase);
    }



    // to insert data into the database
    public long insert_data(String selectTable,String title,String description, String dueDate){
        db=this.getWritableDatabase();
        c= new ContentValues();
        c.put("title",title);
        c.put("description",description);
        c.put("dueDate",dueDate);
        int id = getLastAutoGeneratedKey(selectTable);
        c.put("correspondingTableId",id);
        c.put("correspondingTable",selectTable);
        rowId = db.insert("allTasks", null, c);
        c.clear();
        c.put("title",title);
        c.put("description",description);
        c.put("dueDate",dueDate);
        c.put("correspondingTableId",rowId);  // remember here i am passing the id of allTasks
        c.put("correspondingTable",selectTable); // here i will passing it own tale name so that when i click on officeWork, i know it's office works table from inItemclick

        switch(selectTable){
            case "officeWork":
                rowId = db.insert("officeWork", null, c);
                // Get the latest list of tasks from the database
                officeTasks = getTasks("officeWork");
                officeTasLiveData.postValue(officeTasks);

            case "houseWork":
                rowId = db.insert("houseWork", null, c);
                houseTasks = getTasks("houseWork");
                houseTaskLiveData.postValue(houseTasks);

            case "learning":
                rowId = db.insert("learning", null, c);
                learningTasks = getTasks("learning");
                learningTasksLiveData.postValue(learningTasks);

            case "extraCurr":
                rowId = db.insert("extraCurr", null, c);

                extraCurrTasks = getTasks("extraCurr");
                extraTasksLiveData.postValue(extraCurrTasks);
            default:
                break;


        }
        // Get the latest list of tasks from the database
        allTasks = getTasks("allTasks");
        if(allTasksLiveData==null){
            allTasksLiveData=new MutableLiveData<>();
        }
        // Update the LiveData object with the latest list of tasks
        allTasksLiveData.postValue(allTasks);
        return rowId;

    }
    // how to see the inserted data from the database
    public Cursor getInfo(String selectTable) {
        db = this.getWritableDatabase();
        switch (selectTable) {
            case "office":
                cursor = db.rawQuery("select * from officeWork ", null);
                break;

            case "house":
                cursor = db.rawQuery("select * from houseWork ", null);
                break;

            case "learning":
                cursor = db.rawQuery("select * from learning ", null);
                break;

            case "extra":
                cursor = db.rawQuery("select * from extraCurr ", null);
                break;
            case "doneTasks":
                cursor = db.rawQuery("select * from doneTasks ", null);
                break;

            default:
                cursor = db.rawQuery("select * from allTasks ", null);
                break;
        }

        // close database connection after using the cursor
        return cursor;
    }

    public  List<Task> getTasks(String selectTable) {

        List<Task> tasks = new ArrayList<>();

        Cursor cursor = getInfo(selectTable); // Get the cursor using the existing getInfo() method

        Task task;

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
                @SuppressLint("Range") String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));

                task = new Task(id, title, description, dueDate); // Create a new Task object

                tasks.add(task); // Add the new Task object to the list of tasks
            } while (cursor.moveToNext());
        }

        cursor.close(); // Close the cursor
        Log.d("222222222", "getTasks: "+tasks);
        return tasks;
    }



    public MutableLiveData<List<Task>> getAllTasksLiveData() {
        return allTasksLiveData;
    }
    public MutableLiveData<List<Task>> getOfficeTasksLiveData() {
        return officeTasLiveData;
    }
    public MutableLiveData<List<Task>> getHouseTasksLiveData() {
        return houseTaskLiveData;
    }
    public MutableLiveData<List<Task>> getLearningTasksLiveData() {
        return learningTasksLiveData;
    }
    public MutableLiveData<List<Task>> getExtraCurrTasksLiveData() {
        return extraTasksLiveData;
    }
    public MutableLiveData<List<Task>> getDoneTasksLiveData() {
        return doneTasksLiveData;
    }


    /**Update database(name and password using username)*/
    public boolean updateDatabase(String selectTable, long _id, String title, String description, String dueDate,int corrTabID,String corrTabName, boolean navigation_clicked) {
        db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("title", title);
        c.put("description", description);
        c.put("dueDate", dueDate);
        String selection = null;
        String[] selectionArgs = new String[0];
        String selection1 = null;
        String[] selectionArgs1 = new String[0];
        // Specify the selection criteria as the _id field
        if (navigation_clicked){
            selection = "_id=?";
            selectionArgs = new String[]{String.valueOf(_id)};
            selection1 = "_id=?";
            selectionArgs1 = new String[]{String.valueOf(corrTabID)};
        }
        else{
            selection1 = "_id=?";
            selectionArgs1 = new String[]{String.valueOf(_id)};
            selection = "_id=?";
            selectionArgs = new String[]{String.valueOf(corrTabID)};
        }


        if (corrTabName.equals("allTasks")&& selectTable.equals("allTasks")){
            int numRowsAffected1 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected1 > 0;
        }
        else if (corrTabName.equals("officeWork")&& (selectTable.equals("officeWork")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("officeWork", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else if (corrTabName.equals("houseWork")&& (selectTable.equals("houseWork")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("houseWork", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else if (corrTabName.equals("learning")&& (selectTable.equals("learning")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("learning", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else if (corrTabName.equals("extraCurr")&& (selectTable.equals("extraCurr")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("extraCurr", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else{return false;}

    }
    //Delete a data
    public boolean deleteData(long id, String title, String description, String dueDate,int corrTabID,String corrTabName, boolean navigation_clicked){
        db = this.getWritableDatabase();
        c=new ContentValues();
        c.put("title",title);
        c.put("description",description);
        c.put("dueDate",dueDate);
        c.put("correspondingTable",corrTabName);
        db.insert("doneTasks", null, c);

        doneTasks=getTasks("doneTasks");
        doneTasksLiveData.postValue(doneTasks);
        long r = 0;
        long r1=0;
        long id1;
        int id2;
        Log.d("555555555555555555", "deleteData: "+navigation_clicked + id + " " + corrTabID+ " "+ corrTabName);
        // Specify the selection criteria as the _id field
        if (navigation_clicked){
            id1=(int) id;
            id2=corrTabID;
            Log.d("nonwwwwwwwwwwwwwwwwww", "deleteData: moewwwwwwwwwwwwwwww"+ corrTabID+ " "+ corrTabName);
        }
        else{
            id1=corrTabID;
            id2= (int) id;
        }

        //Delete operation
        try {
            r = db.delete("allTasks", "_id=?", new String[]{String.valueOf(id1)});
            r1= db.delete(""+corrTabName, "_id=?", new String[]{String.valueOf(id2)});
            // Perform database operations...
        } catch (Exception e) {
            Log.e("TAG", "Error occurred while performing database operations: " + e.getMessage());
        }

        Log.d("444444444444", "deleteData: "+navigation_clicked + id1 + " " + id2+ " "+ corrTabName);


        if (r == -1 || r1==-1) {
            return false;
        }
        else {
            return true;
        }


    }

    public int getLastAutoGeneratedKey(String tableName) {
        nextKey = 0;

        if (tableName == null || tableName.isEmpty()) {
            return nextKey;  // Return 0 if tableName is null or empty
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT seq FROM sqlite_sequence WHERE name = '" + tableName + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int currentSeq = cursor.getInt(cursor.getColumnIndex("seq"));
            nextKey = currentSeq + 1;
        } else {
            nextKey = 1;
        }

//        cursor.close();
//        db.close();

        return nextKey;



    }

    public void doneTaskDelete(long id){
        db = this.getWritableDatabase();
        try {
            db.delete("doneTasks", "_id=?", new String[]{String.valueOf(id)});
            // Perform database operations...
        } catch (Exception e) {
            Log.e("TAG", "Error occurred while performing database operations: " + e.getMessage());
        }
    }
}
