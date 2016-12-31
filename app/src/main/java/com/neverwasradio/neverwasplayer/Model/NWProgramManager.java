package com.neverwasradio.neverwasplayer.Model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.neverwasradio.neverwasplayer.Core.XMLProgramParser;
import com.neverwasradio.neverwasplayer.R;

import java.util.ArrayList;

/**
 * Created by chiara on 22/11/15.
 */
public class NWProgramManager {

    public static long lastUpdate;

    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;

    private static NWProgramManager instance;
    private static ArrayList<NWProgram> programList;


    public static void create(Context context){
        GetProgramsTask getProgramsTask = new GetProgramsTask();
        getProgramsTask.execute(context);
        instance=new NWProgramManager();
    }

    public static void load(Context context){
        programList=DBManager.getDBManager(context).getPrograms();
        instance=new NWProgramManager();
    }

    public static NWProgramManager getInstance() {
        return instance;
    }

    public ArrayList<NWProgram> getAllPrograms() { return programList; }

    public ArrayList<NWProgram> getProgramsByDay(int day) {

        ArrayList<NWProgram> result = new ArrayList<NWProgram>();
        for(int i =0; i<programList.size(); i++) {
            if(programList.get(i).getDay()==day) {result.add(programList.get(i));}
        }

        return result;
    }

    public NWProgram getProgramById(String id) {
        for(int i =0; i<programList.size(); i++) {
            if(programList.get(i).getId().compareTo(id)==0) {return programList.get(i);}
        }
        return null;
    }

    private static class GetProgramsTask extends AsyncTask<Context,Object,Context> {

        @Override
        protected Context doInBackground(Context... params) {

            long serverLastUpdate = XMLProgramParser.getLastUpdate();

            if(lastUpdate==0 || lastUpdate<serverLastUpdate) {
                programList = XMLProgramParser.getPrograms();
                if(programList!=null) {
                    DBManager.getDBManager(params[0]).clearTable(DBConstants.ProgramsTable.TABLE_NAME);
                    for (int i = 0; i < programList.size(); i++) {
                        DBManager.getDBManager(params[0]).insertProgram(programList.get(i));
                    }
                }
            }

            else {
                programList=DBManager.getDBManager(params[0]).getPrograms();
            }

            return null;
        }
    }

}
