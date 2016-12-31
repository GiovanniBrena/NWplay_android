package com.neverwasradio.neverwasplayer.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Giovanni on 15/03/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DBConstants.DBNAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String q="CREATE TABLE " + DBConstants.ProgramsTable.TABLE_NAME + " ( "+
                DBConstants.ProgramsTable.PROGRAM_ID + " TEXT PRIMARY KEY not null, " +
                DBConstants.ProgramsTable.TITLE + " TEXT, " +
                DBConstants.ProgramsTable.DESC + " TEXT, " +
                DBConstants.ProgramsTable.DAY + " INTEGER, " +
                DBConstants.ProgramsTable.HOUR + " TEXT, " +
                DBConstants.ProgramsTable.DURATION + " INTEGER, " +
                DBConstants.ProgramsTable.SITE_URL + " TEXT, " +
                DBConstants.ProgramsTable.ICON_URL + " TEXT, " +
                DBConstants.ProgramsTable.ICON_BITMAP + " BLOB, " +
                DBConstants.ProgramsTable.FB_ID + " TEXT) ";
        db.execSQL(q);

        q="CREATE TABLE " + DBConstants.PostTable.TABLE_NAME + " ( "+
                DBConstants.PostTable.TEXT + " TEXT, " +
                DBConstants.PostTable.DATE + " TEXT, " +
                DBConstants.PostTable.INSERT_DATE + " LONG, " +
                DBConstants.PostTable.FB_ID + " TEXT PRIMARY KEY not null, " +
                DBConstants.PostTable.TAG + " TEXT, " +
                DBConstants.PostTable.ICON_BITMAP + " BLOB, " +
                DBConstants.PostTable.IMG_URL + " TEXT) ";
        db.execSQL(q);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }

    @Override
    public synchronized void close() {
        super.close();
    }
}

