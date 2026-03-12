package com.example.ict3214_mobile_application_development_mini_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database ekei, Table ekei nam
    public static final String DATABASE_NAME = "FitnessApp.db";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ACTIVITIES = "activities";

    // Users Table eke Columns tika
    public static final String COL_USER_ID = "ID";
    public static final String COL_USER_NAME = "NAME";
    public static final String COL_USER_EMAIL = "EMAIL";
    public static final String COL_USER_PASSWORD = "PASSWORD";
    public static final String COL_USER_HEIGHT = "HEIGHT";
    public static final String COL_USER_WEIGHT = "WEIGHT";

    // Activities Table eke Columns tika
    public static final String COL_ACT_ID = "ID";
    public static final String COL_ACT_EMAIL = "EMAIL";
    public static final String COL_ACT_NAME = "ACTIVITY_NAME";
    public static final String COL_ACT_DURATION = "DURATION";
    public static final String COL_ACT_DATE = "DATE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table eka create karana SQL query eka
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, EMAIL TEXT, PASSWORD TEXT, HEIGHT TEXT, WEIGHT TEXT)");
        
        // Activities Table eka create karana SQL query eka
        db.execSQL("CREATE TABLE " + TABLE_ACTIVITIES + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, ACTIVITY_NAME TEXT, DURATION TEXT, DATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        onCreate(db);
    }

    // Aluth user kenek save karana method eka (For Sign Up)
    public boolean insertData(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_NAME, name);
        contentValues.put(COL_USER_EMAIL, email);
        contentValues.put(COL_USER_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    // Activity ekak save karana method eka
    public boolean insertActivity(String email, String activityName, String duration, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ACT_EMAIL, email);
        contentValues.put(COL_ACT_NAME, activityName);
        contentValues.put(COL_ACT_DURATION, duration);
        contentValues.put(COL_ACT_DATE, date);

        long result = db.insert(TABLE_ACTIVITIES, null, contentValues);
        return result != -1;
    }

    // Aluth screen eken ena height ekai weight ekai update karana method eka
    public boolean updateUserDetails(String email, String height, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_HEIGHT, height);
        contentValues.put(COL_USER_WEIGHT, weight);

        int result = db.update(TABLE_USERS, contentValues, "EMAIL = ?", new String[]{email});
        return result > 0;
    }

    // Dashboard ekata userge wisthara ganna method eka
    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT NAME, HEIGHT, WEIGHT FROM " + TABLE_USERS + " WHERE EMAIL = ?", new String[]{email});
    }

    // Email ekai Password ekai harida balana method eka (For Login)
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE EMAIL = ? AND PASSWORD = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    // Method to get activities for a user and date
    public Cursor getActivitiesForDate(String email, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT ID, ACTIVITY_NAME, DURATION FROM " + TABLE_ACTIVITIES + " WHERE EMAIL = ? AND DATE = ?", new String[]{email, date});
    }

    // Method to delete an activity by ID
    public boolean deleteActivity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ACTIVITIES, "ID = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // Method to delete all activities for a user on a specific date (useful for sync/replace)
    public void deleteActivitiesForDate(String email, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, "EMAIL = ? AND DATE = ?", new String[]{email, date});
    }
}
