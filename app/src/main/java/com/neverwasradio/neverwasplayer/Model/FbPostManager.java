package com.neverwasradio.neverwasplayer.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.neverwasradio.neverwasplayer.Core.FbPostParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Giovanni on 23/02/16.
 */
public class FbPostManager {

    private static AccessToken token;

    private static FbPostManager instance;
    private static ArrayList<FbPost> postList;

    public static int imagesDownloadCounter = 0;
    public static int totalImages = 10;

    public static FbPostManager create(AccessToken t, Context context){
        instance=new FbPostManager();
        postList=DBManager.getDBManager(context).getPosts(10);
        token=t;
        return instance;
    }

    public FbPostManager() {
        postList=new ArrayList<FbPost>();
        imagesDownloadCounter=0;
        totalImages=10;
    }

    public static FbPostManager getInstance() {
        if(instance==null) {instance=new FbPostManager();}
        return instance;
    }

    public void fetchFacebookPosts(final Context context) {

        final int[] postCounter = new int[1];

        if(instance==null) { return; }

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/204344166256803/posts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                     /* handle the result */
                        JSONArray postArray = null;
                        try {
                            postArray = response.getJSONObject().getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int postLimit = 10;
                        if(postArray.length()<10) {postLimit=postArray.length();}

                        for (int i=0; i<postLimit; i++) {
                            String postId;
                            try {
                                postId = postArray.getJSONObject(i).getString("id");

                                final int finalI = i;
                                final JSONArray finalPostArray = postArray;

                                // se non esiste in DB
                                if(DBManager.getDBManager(context).getPostById(postId)==null) {
                                    Log.e("FB POST MANAGER", "New post found");

                                    new GraphRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            "/" + postId + "/attachments",
                                            null,
                                            HttpMethod.GET,
                                            new GraphRequest.Callback() {
                                                public void onCompleted(GraphResponse response) {
                                             /* handle the result */
                                                    try {
                                                        FbPost p = FbPostParser.parseFbPostResponse(finalPostArray.getJSONObject(finalI), response.getJSONObject().getJSONArray("data"));
                                                        if (p != null && DBManager.getDBManager(context).getPostById(p.getId()) == null) {
                                                            postList.add(p);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                    ).executeAndWait();
                                }
                                else {
                                    Log.e("FB POST MANAGER", "Post skipped");
                                    totalImages--;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAndWait();
    }

    public boolean downloadPostImage(FbPost post, Context context) {
        try {
            InputStream in = new java.net.URL(post.getImgUrl()).openStream();
            post.setImg(BitmapFactory.decodeStream(in));

        } catch (Exception e) {
            e.printStackTrace();
            post.setHasImage(false);
            return false;
        }
        return true;
    }

    public ArrayList<FbPost> getAllPosts() {
        return postList;
    }

    public void resetFbManager() {
        postList.clear();
    }


}
