package com.neverwasradio.neverwasplayer.UI.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.neverwasradio.neverwasplayer.Model.NWProgram;
import com.neverwasradio.neverwasplayer.Model.NWProgramManager;
import com.neverwasradio.neverwasplayer.R;

public class ProgramDetailActivity extends Activity {

    NWProgram program;
    NWProgramManager programManager;

    ImageView icon, fbLogo;
    TextView title, desc, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail2);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        programManager = NWProgramManager.getInstance();
        program=programManager.getProgramById(intent.getStringExtra("id"));

        icon = (ImageView) findViewById(R.id.pDetailImage);
        fbLogo = (ImageView) findViewById(R.id.pDetailFbLogo);
        title = (TextView) findViewById(R.id.pDetailTitle);
        desc = (TextView) findViewById(R.id.pDetailDesc);
        date = (TextView) findViewById(R.id.pDetailDate);

        icon.setImageBitmap(program.getIcon());
        title.setText(program.getName());
        desc.setText(program.getDesc());
        String day = "";
        if(program.getDay()==0) {day="Lunedì";}
        else if(program.getDay()==1) {day="Martedì";}
        else if(program.getDay()==2) {day="Mercoledì";}
        date.setText("Tutti i " + day+ " alle " + program.getHour());

        initListeners();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, icon, getString(R.string.program_icon_trans));
            }
            return true;
        }
        return false;
    }

    private void initListeners()
    {
        fbLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent;

                if(program.getFbId().compareTo("")!=0) {
                    try {
                        getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                        browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + program.getFbId()));
                    } catch (Exception e) {
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(program.getSiteUrl()));
                    }
                }
                else { browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(program.getSiteUrl())); }

                startActivity(browserIntent);
            }
        });
    }

}
