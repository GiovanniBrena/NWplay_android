package com.neverwasradio.neverwasplayer.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.neverwasradio.neverwasplayer.Core.FbPostParser;
import com.neverwasradio.neverwasplayer.Model.DBManager;
import com.neverwasradio.neverwasplayer.Model.FbPost;
import com.neverwasradio.neverwasplayer.Model.FbPostManager;
import com.neverwasradio.neverwasplayer.Model.NWProgram;
import com.neverwasradio.neverwasplayer.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsActivity extends Activity {

    FbPostManager fbPostManager;

    AccessToken token;

    LoginButton loginButton;
    CallbackManager callbackManager;

    RelativeLayout loginLayout, progressLayout, postListLayout, progressSmall;
    LinearLayout mainLayout;

    ListView postListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = (LinearLayout) findViewById(R.id.newsMainLayout);
        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        progressLayout = (RelativeLayout) findViewById(R.id.newsProgressLayout);
        progressSmall = (RelativeLayout) findViewById(R.id.newsProgressSmall);
        postListLayout = (RelativeLayout) findViewById(R.id.newsListLayout);
        postListView = (ListView) findViewById(R.id.newsListView);

        progressSmall.setVisibility(View.VISIBLE);

        token = AccessToken.getCurrentAccessToken();

        // user is logged
        if(token!=null) {
            loginLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            fbPostManager = FbPostManager.create(token,this);


            // DB has posts, show them
            if(fbPostManager.getAllPosts().size()>0) {
                postListLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list, fbPostManager.getAllPosts()));
            }

            // DB has no posts, show progress
            else {
                postListLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
            }

            if(MainActivity.isNetworkConnected(this)) {
                // begin synchronization
                GetPostsAsyncTask getPostsAsyncTask = new GetPostsAsyncTask();
                getPostsAsyncTask.execute(getApplicationContext());
            }
        }

        // user not logged, show log in view
        else {
            mainLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        }



        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");

        // Callback registration
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.e("Facebook-SDK", "login successful");
                AccessToken accessToken = loginResult.getAccessToken();
                Log.e("FB token", accessToken.getToken());
                Log.e("FB user id", accessToken.getUserId());
                Log.e("FB app id", accessToken.getApplicationId());

                token = AccessToken.getCurrentAccessToken();

                fbPostManager = FbPostManager.create(token,getApplicationContext());

                if(fbPostManager.getAllPosts().size()>0) {
                    postListLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list, fbPostManager.getAllPosts()));
                }

                if(MainActivity.isNetworkConnected(getApplicationContext())) {
                    GetPostsAsyncTask getPostsAsyncTask = new GetPostsAsyncTask();
                    getPostsAsyncTask.execute(getApplicationContext());
                }


            }
            @Override
            public void onCancel() {
                // App code
                Log.e("Facebook-SDK", "login canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    mainLayout.setVisibility(View.GONE);
                    loginLayout.setVisibility(View.VISIBLE);

                    token = null;
                }

                else {
                    loginLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);

                    token = AccessToken.getCurrentAccessToken();
                }
            }
        };
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public class GetPostsAsyncTask extends AsyncTask<Context, Void, Context> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //postListLayout.setVisibility(View.GONE);
            //progressLayout.setVisibility(View.VISIBLE);
            Log.e("FB POSTS", "begin fetch posts");
        }

        @Override
        protected Context doInBackground(Context... context) {

            fbPostManager.fetchFacebookPosts(context[0]);
            return context[0];
        }

        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);

            Log.e("FB POSTS", "fetch posts completed");

            ArrayList<FbPost> posts = fbPostManager.getAllPosts();
            for (int i=0; i<posts.size(); i++) {
                if(!posts.get(i).isLocal()) {
                    DownloadImagesTask downloadImagesTask = new DownloadImagesTask();
                    downloadImagesTask.execute(posts.get(i), context);
                }
            }
            if(FbPostManager.totalImages==0) {
                postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list, fbPostManager.getAllPosts()));
                progressLayout.setVisibility(View.GONE);
                progressSmall.setVisibility(View.GONE);
                postListLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class DownloadImagesTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            return fbPostManager.downloadPostImage((FbPost)params[0], (Context)params[1]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) { FbPostManager.imagesDownloadCounter++;}
            else {FbPostManager.totalImages--;}

            Log.e("IMG Download", "image downloaded, remaining: "+String.valueOf(FbPostManager.totalImages - FbPostManager.imagesDownloadCounter));

            if(FbPostManager.imagesDownloadCounter==FbPostManager.totalImages) {

                // Insert into db
                for(int i=0; i<FbPostManager.getInstance().getAllPosts().size(); i++) {
                    if (DBManager.getDBManager(getApplicationContext()).getPostById(FbPostManager.getInstance().getAllPosts().get(i).getId()) == null) {
                        DBManager.getDBManager(getApplicationContext()).insertPost(FbPostManager.getInstance().getAllPosts().get(i));
                    }
                }

                postListView.setAdapter(new NewsListAdapter(getApplicationContext(), R.layout.item_post_list, fbPostManager.getAllPosts()));
                progressLayout.setVisibility(View.GONE);
                progressSmall.setVisibility(View.GONE);
                postListLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class NewsListAdapter extends ArrayAdapter<FbPost> {

        ArrayList<FbPost> programs;

        public NewsListAdapter(Context context, int resource, ArrayList<FbPost> objects) {
            super(context, resource, objects);
            programs=objects;
        }

        @Override
        public int getCount()
        {
            return programs.size();
        }

        @Override
        public FbPost getItem(int position)
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
                v= LayoutInflater.from(getContext()).inflate(R.layout.item_post_list, null);
            }

            final FbPost p=(FbPost) getItem(position);

            TextView label=(TextView) v.findViewById(R.id.postItemDateLabel);
            label.setText(p.getDate());
            label.setVisibility(View.GONE);

            label=(TextView) v.findViewById(R.id.postItemTextLabel);
            label.setText(p.getText());

            label=(TextView) v.findViewById(R.id.postItemTagLabel);
            String s="Con ";

            if(p.getTag()!=null) {
                for (int i = 0; i < p.getTag().length; i++) {
                    if(i==p.getTag().length-1) {s = s + p.getTag()[i];}
                    else {s = s + p.getTag()[i] + ", ";}
                }
                label.setText(s);
            }
            else {label.setText("");}

            ImageView icon = (ImageView) v.findViewById(R.id.postItemImage);
            icon.setImageBitmap(p.getImg());


            return v;
        }
    }

}
