package com.example.a32150.phonebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DB {
    private static final String DATABASE_NAME = "phonebook.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "person";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
        + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR, phone VARCHAR, email VARCHAR);";

    private Context mCtx;
    private DataBaseOpenHelper helper;
    private SQLiteDatabase db;

    public DB(Context context) {
        mCtx = context;
    }

    public DB open() {
        helper = new DataBaseOpenHelper(mCtx);
        db = helper.getReadableDatabase();
        return this;
    }

    private class DataBaseOpenHelper extends SQLiteOpenHelper {

        public DataBaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    private static final String KEY_ROWID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CITY = "phone";
    private static final String KEY_SITE = "email";

    String[] strCols = {KEY_ROWID, KEY_NAME,KEY_CITY, KEY_SITE};

    public Cursor getAll() {
        return db.query(TABLE_NAME, strCols, null, null, null, null, null);
    }

    public Cursor get(long rowId) throws SQLException {
        Cursor mCursor = db.query(true,
            TABLE_NAME,
            strCols,
            KEY_ROWID + "=" + rowId,
            null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public long create(String name, String phone, String email) {
        Date now = new Date();
        ContentValues args = new ContentValues();
        //args.put(KEY_NOTE, noteName);
        args.put(KEY_NAME, name);
        args.put(KEY_CITY, phone);
        args.put(KEY_SITE, email);
        return db.insert(TABLE_NAME, null, args);
    }

    public boolean delete(long rowId) {
        return db.delete(TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean update(long rowId, String name, String phone, String email) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_CITY, phone);
        args.put(KEY_SITE, email);
        return db.update(TABLE_NAME, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
