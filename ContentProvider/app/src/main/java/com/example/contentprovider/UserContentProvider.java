package com.example.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;

public class UserContentProvider extends ContentProvider {
    private String Tag="UserContentProvider: ";
    public static final String AUTHORITY = "com.example.contentprovider";
    public static final Uri UIER_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/user");
    public static final Uri STUDENT_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/student");
    public static final int USER_URI_CODE = 0;
    public static final int STUDENT_URI_CODE = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE);
        uriMatcher.addURI(AUTHORITY, "student", STUDENT_URI_CODE);
    }
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    public boolean onCreate() {
        Log.i(Tag,Thread.currentThread().getName());
        context=getContext();
        initProviderData();
        return false;
    }
    private void initProviderData() {
        sqLiteDatabase = new DBOpenHelper(context).getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + DBOpenHelper.USER_TALBE_NAME);
        sqLiteDatabase.execSQL("delete from " + DBOpenHelper.STUDENT_TABLE_NAME);
        sqLiteDatabase.execSQL("insert into user values(1,'张三');");
        sqLiteDatabase.execSQL("insert into user values(2,'李四');");
        sqLiteDatabase.execSQL("insert into student values(1,'张三','80');");
        sqLiteDatabase.execSQL("insert into student values(2,'李四','90');");
        sqLiteDatabase.execSQL("insert into student values(3,'王五','100');");
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(Tag,Thread.currentThread().getName());
        String table = getTableName(uri);
        return sqLiteDatabase.query(table,projection,selection,selectionArgs,null,null,sortOrder,null);
    }


    @Override
    public String getType( Uri uri) {
        Log.i(Tag,"getType");

        return null;
    }


    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        Log.i(Tag,"insert");
        String table = getTableName(uri);
        sqLiteDatabase.insert(table, null, values);
        context.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        Log.i(Tag,"delete");
        String table = getTableName(uri);
        int count = sqLiteDatabase.delete(table, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        Log.i(Tag,"update");
        String table = getTableName(uri);
        int row = sqLiteDatabase.update(table, values, selection, selectionArgs);
        if (row > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return row;
    }
    private String getTableName(Uri uri) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {
            case STUDENT_URI_CODE:
                tableName = DBOpenHelper.STUDENT_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DBOpenHelper.USER_TALBE_NAME;
                break;
            default:break;
        }

        return tableName;
    }
}
