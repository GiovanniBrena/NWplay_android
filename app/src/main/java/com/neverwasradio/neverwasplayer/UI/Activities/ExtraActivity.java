package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neverwasradio.neverwasplayer.Model.DBConstants;
import com.neverwasradio.neverwasplayer.Model.DBManager;
import com.neverwasradio.neverwasplayer.Model.FbPost;
import com.neverwasradio.neverwasplayer.Model.FbPostManager;
import com.neverwasradio.neverwasplayer.R;

import java.io.File;
import java.text.DecimalFormat;

public class ExtraActivity extends Activity {

    ImageView shareIcon, rateIcon;
    TextView shareLb, rateLb, memoryLb;
    RelativeLayout bugsButton, contactButton, clearDbButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        shareIcon = (ImageView) findViewById(R.id.extraShareIcon);
        rateIcon = (ImageView) findViewById(R.id.extraRateIcon);
        shareLb = (TextView) findViewById(R.id.extraShareLb);
        rateLb = (TextView) findViewById(R.id.extraRateLb);
        memoryLb = (TextView) findViewById(R.id.extraMemoryLabel);
        bugsButton = (RelativeLayout) findViewById(R.id.extraBugBt);
        contactButton = (RelativeLayout) findViewById(R.id.extraContactBt);
        clearDbButton = (RelativeLayout) findViewById(R.id.clearDbButton);


        float dbSize = Float.valueOf(DBManager.getDBManager(getApplicationContext()).getDbSizeByte(getApplicationContext()))/1000000;
        memoryLb.setText("Dati in memoria: " + new DecimalFormat("#,##").format(dbSize) + "Mb");

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
        View.OnClickListener shareListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Scarica Neverwas Play! gratis su Google Play Store");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        };
        shareLb.setOnClickListener(shareListener);
        shareIcon.setOnClickListener(shareListener);

        View.OnClickListener rateListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), " unable to find market app", Toast.LENGTH_LONG).show();
                }
            }
        };
        rateLb.setOnClickListener(rateListener);
        rateIcon.setOnClickListener(rateListener);

        bugsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                String body="Segnalazione Problema NWPlay " + version;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "brena.giovanni@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                String body="";

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "brena.giovanni@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });

        clearDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DBManager.getDBManager(getApplicationContext()).clearTable(DBConstants.ProgramsTable.TABLE_NAME);
                DBManager.getDBManager(getApplicationContext()).clearTable(DBConstants.PostTable.TABLE_NAME);

                FbPostManager.getInstance().resetFbManager();
                SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
                editor.putLong("lastUpdate", 0);
                editor.apply();

                Toast.makeText(getApplicationContext(), "Database Cleared",
                        Toast.LENGTH_SHORT).show();

                float dbSize = Float.valueOf(DBManager.getDBManager(getApplicationContext()).getDbSizeByte(getApplicationContext()))/1000000;
                memoryLb.setText("Dati in memoria: " + new DecimalFormat("#,##").format(dbSize) + "Mb");
            }
        });
    }

}
