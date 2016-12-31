package com.neverwasradio.neverwasplayer.Core;

import android.util.Log;

import com.facebook.GraphResponse;
import com.neverwasradio.neverwasplayer.Model.FbPost;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Giovanni on 23/02/16.
 */
public class FbPostParser {

    public static FbPost parseFbPostResponse(JSONObject post, JSONArray attach)  {
        FbPost p;

        String id = null;
        String text = null;
        String date = null;

        try {
            id = post.getString("id");
            date = post.getString("created_time");
            text = post.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Post parsing", "error, skip post");
            return null;
        }

        String[] tag = null;

        JSONArray tags = null;
        try {
            tags = attach.getJSONObject(0).getJSONArray("description_tags");
            if(tags.length()>0) {
                tag = new String[tags.length()];
                for (int i = 0; i < tags.length(); i++) {
                    tag[i]=tags.getJSONObject(i).get("name").toString();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String img = null;
        try {
            img = attach.getJSONObject(0).getJSONObject("media").getJSONObject("image").getString("src");
        } catch (JSONException e) {
            e.printStackTrace();
            img=null;
        }

        p = new FbPost(id,text,date,img,tag);


        return p;
    }

}
