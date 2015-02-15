package com.tech_mail.tp_android_2015;

/**
 * Created by max on 15.02.15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.sql.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseHelper";

    public static final String TRANS_TABLE_NAME = "translations";
    public static final String REQUEST_LANG = "request_lang";
    public static final String REQUEST = "request";
    public static final String TRANS_LANG = "trans_lang";
    public static final String TRANS = "trans";
    public static final String TIMESTAMP = "time";

    public DatabaseHelper(Context context, CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    public DatabaseHelper(Context context, CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
    }

    private static final String DATABASE_CREATE_SCRIPT = "CREATE TABLE "
            + TRANS_TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + REQUEST_LANG + " VARCHAR(100) NOT NULL, "
            + REQUEST + " VARCHAR(100) NOT NULL, "
            + TRANS_LANG + " VARCHAR(100) NOT NULL, "
            + TRANS + " VARCHAR(100) NOT NULL, "
            + TIMESTAMP + "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    private static final String DATABASE_DROP_SCRIPT = "DROP TABLE IF EXISTS "
            + TRANS_TABLE_NAME + ";";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DROP_SCRIPT);
        onCreate(db);
    }

    public void insert(String reqLang, String request, String transLang, String trans) {
        ContentValues map = new ContentValues();
        map.put(REQUEST_LANG, reqLang);
        map.put(REQUEST, request);
        map.put(TRANS_LANG, transLang);
        map.put(TRANS, trans);
        getWritableDatabase().insert(TRANS_TABLE_NAME, null, map);
    }

    public void delete(String request) {
        String [] whereArgs = new String [] {request};
        getWritableDatabase().delete(TRANS_TABLE_NAME, REQUEST + "=?", whereArgs);
    }

    public boolean deleteAll() {
        int doneDelete = 0;
        doneDelete = getWritableDatabase().delete(TRANS_TABLE_NAME, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public Cursor fetchAll() {
        SQLiteDatabase sdb = getReadableDatabase();
        Cursor cursor = sdb.query(TRANS_TABLE_NAME, new String[] {BaseColumns._ID,
                        REQUEST_LANG, REQUEST, TRANS_LANG, TRANS},
                null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}