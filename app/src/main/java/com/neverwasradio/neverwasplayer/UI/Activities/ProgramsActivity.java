package com.neverwasradio.neverwasplayer.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.neverwasradio.neverwasplayer.Core.XMLProgramParser;
import com.neverwasradio.neverwasplayer.Model.NWProgram;
import com.neverwasradio.neverwasplayer.Model.NWProgramManager;
import com.neverwasradio.neverwasplayer.R;

import java.util.ArrayList;

public class ProgramsActivity extends Activity {

    ProgramsActivity thisActivity;

    NWProgramManager programManager;

    private ListView listView;
    private ProgramListAdapter programListAdapter;

    private ImageView buttLune, buttMarte, buttMerco;
    private RelativeLayout progressLayout, noConnectionLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programs);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        thisActivity=this;

        programManager=NWProgramManager.getInstance();

        listView= (ListView) findViewById(R.id.programsListView);
        buttLune = (ImageView) findViewById(R.id.programsLuneIcon);
        buttMarte = (ImageView) findViewById(R.id.programsMarteIcon);
        buttMerco = (ImageView) findViewById(R.id.programsMercoIcon);

        progressLayout = (RelativeLayout) findViewById(R.id.programsProgressLayout);

        buttLune.setImageResource(R.drawable.lun_full);

        if(programManager==null && !MainActivity.isNetworkConnected(getApplicationContext())) {
            listView.setVisibility(View.GONE);
            noConnectionLayout.setVisibility(View.VISIBLE);
        }

        else if(programManager==null) {
            //new getProgramsTask().execute();
        }
        else {
            programListAdapter = new ProgramListAdapter(getApplicationContext(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.MONDAY));
            listView.setAdapter(programListAdapter);
        }

        initListeners();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_on_right);
            return true;
        }
        return false;
    }

    private void initListeners(){

        buttLune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(programManager==null) {return;}

                buttLune.setImageResource(R.drawable.lun_full);
                buttMarte.setImageResource(R.drawable.mar_blank);
                buttMerco.setImageResource(R.drawable.mer_blank);

                programListAdapter = new ProgramListAdapter(getApplicationContext(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.MONDAY));
                listView.setAdapter(programListAdapter);
            }
        });

        buttMarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(programManager==null) {return;}

                buttLune.setImageResource(R.drawable.lun_blank);
                buttMarte.setImageResource(R.drawable.mar_full);
                buttMerco.setImageResource(R.drawable.mer_blank);

                programListAdapter = new ProgramListAdapter(getApplicationContext(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.TUESDAY));
                listView.setAdapter(programListAdapter);
            }
        });

        buttMerco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(programManager==null) {return;}

                buttLune.setImageResource(R.drawable.lun_blank);
                buttMarte.setImageResource(R.drawable.mar_blank);
                buttMerco.setImageResource(R.drawable.mer_full);

                programListAdapter = new ProgramListAdapter(getApplicationContext(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.WEDNESDAY));
                listView.setAdapter(programListAdapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NWProgram p = programListAdapter.getItem(position);

                // get image
                ImageView icon = (ImageView) view.findViewById(R.id.programIcon);

                //Intent myIntent = new Intent(getApplicationContext(), ProgramDetailActivity.class);
                //myIntent.putExtra("id", p.getId()); //Optional parameters
                //startActivity(myIntent);

                Intent intent = new Intent(getApplicationContext(), ProgramDetailActivity.class);
                intent.putExtra("id", p.getId()); //Optional parameters

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(thisActivity, icon, getString(R.string.program_icon_trans));
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        });

    }


    private class ProgramListAdapter extends ArrayAdapter<NWProgram> {

        ArrayList<NWProgram> programs;

        public ProgramListAdapter(Context context, int resource, ArrayList<NWProgram> objects) {
            super(context, resource, objects);
            programs=objects;
        }

        @Override
        public int getCount()
        {
            return programs.size();
        }

        @Override
        public NWProgram getItem(int position)
        {
            return programs.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            if (v==null)
            {
                v= LayoutInflater.from(getContext()).inflate(R.layout.item_programs_list, null);
            }

            NWProgram p=(NWProgram) getItem(position);

            TextView label=(TextView) v.findViewById(R.id.programTitle);
            label.setText(p.getName());
            label=(TextView) v.findViewById(R.id.programData);
            label.setText(p.getHour());

            ImageView icon = (ImageView) v.findViewById(R.id.programIcon);
            icon.setImageBitmap(p.getIcon());
            return v;
        }
    }
/*
    private class getProgramsTask extends AsyncTask<Object,Object,Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            Log.e("PROGRAMS", "begin download programs data");
        }

        @Override
        protected Object doInBackground(Object... params) {
            NWProgramManager.create();
            programManager = NWProgramManager.getInstance();

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            programListAdapter = new ProgramListAdapter(getApplicationContext(),R.layout.item_programs_list,programManager.getProgramsByDay(NWProgramManager.MONDAY));
            listView.setAdapter(programListAdapter);

            progressLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

        }
    }
*/

}
