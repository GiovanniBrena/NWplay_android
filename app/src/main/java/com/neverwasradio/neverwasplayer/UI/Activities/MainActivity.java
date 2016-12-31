package com.neverwasradio.neverwasplayer.UI.Activities;


import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.analytics.tracking.android.EasyTracker;
import com.neverwasradio.neverwasplayer.Core.PlayerService;
import com.neverwasradio.neverwasplayer.Model.DBConstants;
import com.neverwasradio.neverwasplayer.Model.DBManager;
import com.neverwasradio.neverwasplayer.Model.NWProgramManager;
import com.neverwasradio.neverwasplayer.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements ActionBar.TabListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static SharedPreferences sharedPref;

    public static boolean playerLock;
    public static PlayerServiceLauncher serviceLauncher;

    public enum PlayState {
        PLAY,
        STOP,
        BUFFERING,
        ERROR,
        NETWORK_ERROR
    }


    private void setMainUI() {
        LayoutInflater inflator=getLayoutInflater();
        View view=inflator.inflate(R.layout.activity_main, null, false);
        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        setContentView(view);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    private void setLoadingUI(final MainActivity activity){
        setContentView(R.layout.activity_main_loading_intro);
        Timer loadingScreenTimer = new Timer();
        loadingScreenTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMainUI();
                    }
                });
            }
        }, 5000);
    }

    private void setIntroUI(){
        setContentView(R.layout.activity_main_welcome_intro);

        final RelativeLayout r1,r2,r3,r4,r5;

        r1 = (RelativeLayout) findViewById(R.id.introLayout1);
        r2 = (RelativeLayout) findViewById(R.id.introLayout2);
        r3 = (RelativeLayout) findViewById(R.id.introLayout3);
        r4 = (RelativeLayout) findViewById(R.id.introLayout4);
        r5 = (RelativeLayout) findViewById(R.id.introLayout5);

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r1.setVisibility(View.GONE);
                r2.setVisibility(View.VISIBLE);
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r2.setVisibility(View.GONE);
                r3.setVisibility(View.VISIBLE);
            }
        });
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r3.setVisibility(View.GONE);
                r4.setVisibility(View.VISIBLE);
            }
        });
        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r4.setVisibility(View.GONE);
                r5.setVisibility(View.VISIBLE);
            }
        });
        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("first_run", false);
                editor.commit();
                setMainUI();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean firstRun = sharedPref.getBoolean("first_run", true);

        if(firstRun) {setIntroUI();}
        else {setLoadingUI(this);}


        /* INITIALIZE SERVICES */
        cleanDB();

        /* Init Facebook SDK */
        FacebookSdk.sdkInitialize(getApplicationContext());

        /* Init ProgramsManager */
        NWProgramManager.lastUpdate = sharedPref.getLong("lastUpdate",0);
        Log.e("NW PROGRAM MANAGER", "Local lastUpdate: "+NWProgramManager.lastUpdate );
        NWProgramManager.create(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("lastUpdate", System.currentTimeMillis());
        editor.apply();
        Log.e("NW PROGRAM MANAGER", "NEW Local lastUpdate: " + System.currentTimeMillis());


        Timer preLoadingTimer;
        serviceLauncher = new PlayerServiceLauncher();


        if(!PlayerService.isActive() && !PlayerService.isLoading() && !PlayerService.isReady() && PlayerService.isFistRun() && isNetworkConnected(getApplicationContext())) {

            preLoadingTimer=new Timer();
            preLoadingTimer.schedule(new PreLoadingTimerTask(),1000*40);

            serviceLauncher.execute(PlayState.BUFFERING);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PlayerService.isActive()) {
            PlayerFragment.getInstance().updatePlayerIcon(PlayState.PLAY);
        }
        else if (PlayerService.isLoading()) {
            PlayerFragment.getInstance().updatePlayerIcon(PlayState.BUFFERING);
        }
        else {PlayerFragment.getInstance().updatePlayerIcon(PlayState.STOP);}
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getDBManager(this).closeDb();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return PlayerFragment.getInstance();
                case 1:
                    return InfoFragment.getInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ASCOLTA";
                case 1:
                    return "SCOPRI";
            }
            return null;
        }
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        boolean connected = ni != null && ni.isConnectedOrConnecting();

        if (!connected) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private static class PlayerServiceLauncher extends AsyncTask<PlayState, Void, PlayState> {

        @Override
        protected PlayState doInBackground(PlayState... strings) {
            PlayerService.initializePlayer();
            return strings[0];
        }

        @Override
        protected void onPostExecute(PlayState state) {
            super.onPostExecute(state);

            if(state.compareTo(PlayState.BUFFERING)==0 && PlayerService.isActive() && !PlayerService.isError()) {
                PlayerFragment.getInstance().updatePlayerIcon(PlayState.PLAY);
            }
            else if(PlayerService.isError()) { PlayerFragment.getInstance().updatePlayerIcon(PlayState.ERROR); }

            playerLock=false;

        }
    }

    public static void executePreloding() {
        serviceLauncher = new PlayerServiceLauncher();
        serviceLauncher.execute(MainActivity.PlayState.BUFFERING);
    }

    public static AsyncTask.Status getPreloadingStatus() {
        if(serviceLauncher==null) {return null;}
        return serviceLauncher.getStatus();
    }


    class PreLoadingTimerTask extends TimerTask
    {
        @Override
        public void run() {
            if(!PlayerService.isActive() && (PlayerService.isReady()||PlayerService.isLoading())) {
                PlayerService.resetPlayer();
            }
        }
    }

    private void cleanDB(){
        if(DBManager.getDBManager(getApplicationContext()).getPosts(200).size()>50) {
            DBManager.getDBManager(getApplicationContext()).clearTable(DBConstants.PostTable.TABLE_NAME);
        }
    }
}
