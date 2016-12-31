package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.neverwasradio.neverwasplayer.R;

public class SocialActivity extends Activity {

    RelativeLayout websiteBt, emailBt;
    ImageView fbBtn, twBtn, ytBtn, instaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        fbBtn = (ImageView) findViewById(R.id.socialFbBt);
        twBtn = (ImageView) findViewById(R.id.socialTwBt);
        ytBtn = (ImageView) findViewById(R.id.socialYtBt);
        instaBtn = (ImageView) findViewById(R.id.socialInstaBt);
        websiteBt = (RelativeLayout) findViewById(R.id.socialWebsiteBt);
        emailBt = (RelativeLayout) findViewById(R.id.socialEmailBt);

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

    private void initListeners() {
        emailBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                String body="Contatto da NeverwasPlay Android " + version;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "info@associazionesmart.it", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, body);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });

        websiteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.associazionesmart.it/"));
                startActivity(browserIntent);
            }
        });

        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/204344166256803"));
                } catch (Exception e) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/neverwasradio"));
                }
                startActivity(intent);
            }
        });

        twBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=895226899"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/neverwasradio"));
                }
                startActivity(intent);

            }
        });

        instaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/neverwasradio");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/neverwasradio")));
                }
            }
        });

        ytBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCylU-RnBuLz4nzdA5sNdFYw"));
                startActivity(intent);
            }
        });

    }

}
