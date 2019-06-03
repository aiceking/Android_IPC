package com.example.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "user_provider.db";
    private static final int DB_VERSION=1;
    public static final String STUDENT_TABLE_NAME = "student";
    public static final String USER_TALBE_NAME = "user";

    private String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS "
            + USER_TALBE_NAME + "(_id INTEGER PRIMARY KEY," + "name TEXT)"
            ;
    private String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS "
            + STUDENT_TABLE_NAME + "(_id INTEGER PRIMARY KEY," + "name TEXT,grade TEXT)";

    public DBOpenHelper(Context context){
    super(context,DB_NAME,null,DB_VERSION);
}
    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_USER_TABLE);
    db.execSQL(CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
