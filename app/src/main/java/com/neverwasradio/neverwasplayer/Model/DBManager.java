package com.neverwasradio.neverwasplayer.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.neverwasradio.neverwasplayer.Utilities.DBBitmapUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Giovanni on 15/03/16.
 */
public class DBManager {
    private static DBManager instance = null; // Singleton control
    private static DBHelper dbhelper;
    private static SQLiteDatabase db;

    /* Constructors */
    public static DBManager getDBManager(Context ctx) {
        if(instance==null)
        {
            instance=new DBManager(ctx);
            return instance;
        }
        return instance;
    }
    private DBManager(Context ctx) {

        dbhelper=new DBHelper(ctx);
        db = dbhelper.getReadableDatabase();

    }

    public void closeDb () {
        db.close();
        dbhelper.close();
        instance=null;
    }


    /* DB Add Elements */
    public void insertProgram(NWProgram element) {

        Log.e("DB-MANAGER", "Adding to DB program: " + element.getName());

        ContentValues values = new ContentValues();
        values.put(DBConstants.ProgramsTable.PROGRAM_ID, element.getId());
        values.put(DBConstants.ProgramsTable.TITLE, element.getName());
        values.put(DBConstants.ProgramsTable.DESC, element.getDesc());
        values.put(DBConstants.ProgramsTable.DAY, element.getDay());
        values.put(DBConstants.ProgramsTable.DURATION, element.getDuration());
        values.put(DBConstants.ProgramsTable.FB_ID, element.getFbId());
        values.put(DBConstants.ProgramsTable.HOUR, element.getHour());
        values.put(DBConstants.ProgramsTable.ICON_URL, element.getIconUrl());
        values.put(DBConstants.ProgramsTable.ICON_BITMAP, DBBitmapUtility.getBytes(element.getIcon()));
        values.put(DBConstants.ProgramsTable.SITE_URL, element.getSiteUrl());

        long insResult = db.insert(DBConstants.ProgramsTable.TABLE_NAME, null, values);

    }

    public void insertPost(FbPost element) {

        Log.e("DB-MANAGER", "Adding to DB post: " + element.getId());

        ContentValues values = new ContentValues();
        values.put(DBConstants.PostTable.FB_ID, element.getId());
        values.put(DBConstants.PostTable.TEXT, element.getText());
        values.put(DBConstants.PostTable.DATE, element.getDate());
        values.put(DBConstants.PostTable.INSERT_DATE, element.getInsertDate());
        values.put(DBConstants.PostTable.IMG_URL, element.getImgUrl());
        values.put(DBConstants.PostTable.INSERT_DATE, element.getInsertDate());
        String tag="";
        if(element.getTag()!=null && element.getTag().length>0) {
            for (int i = 0; i < element.getTag().length; i++) {
                tag = tag + element.getTag()[i] + "%";
            }
            values.put(DBConstants.PostTable.TAG, tag);
        }

        if(element.getImg()!=null) {
            values.put(DBConstants.ProgramsTable.ICON_BITMAP, DBBitmapUtility.getBytes(element.getImg()));
        }
        long insResult = db.insert(DBConstants.PostTable.TABLE_NAME, null, values);
    }

    public ArrayList<NWProgram> getPrograms(){
        Cursor cursor;
        NWProgram element;
        ArrayList<NWProgram> results = new ArrayList<NWProgram>();

        cursor=db.query(DBConstants.ProgramsTable.TABLE_NAME, null, null, null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast())
        {
            element=new NWProgram(cursor);
            results.add(element);
            cursor.moveToNext();
        }


        cursor.close();
        return results;
    }

    public FbPost getPostById(String id){
        Cursor cursor;
        FbPost element=null;

        cursor=db.query(
                DBConstants.PostTable.TABLE_NAME,
                null,
                DBConstants.PostTable.FB_ID+"= ?",
                new String[] {id},
                null, null, null, null);

        if(cursor.getCount()==0) {
            cursor.close();
            return null;
        }

        cursor.moveToFirst();
        element=new FbPost(cursor);

        cursor.close();
        return element;
    }

    public ArrayList<FbPost> getPosts(int numberOfPosts){
        Cursor cursor;
        FbPost element;
        ArrayList<FbPost> results = new ArrayList<FbPost>();

        cursor=db.query(DBConstants.PostTable.TABLE_NAME, null, null, null, null, null, DBConstants.PostTable.INSERT_DATE +" ASC", String.valueOf(numberOfPosts));
        cursor.moveToFirst();

        while(!cursor.isAfterLast())
        {
            element=new FbPost(cursor);
            results.add(element);
            cursor.moveToNext();
        }


        cursor.close();
        return results;
    }


    public void clearTable(String tableName) {
        Log.e("DB MANAGER", "clearing table: " + tableName);
        db.execSQL("delete from " + tableName);
    }

    public long getDbSizeByte(Context context){
        File f = context.getDatabasePath(DBConstants.DBNAME);
        long dbSize = f.length();

        return dbSize;
    }
}

