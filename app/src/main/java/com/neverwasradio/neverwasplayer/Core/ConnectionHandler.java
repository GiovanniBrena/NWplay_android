package com.neverwasradio.neverwasplayer.Core;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Giovanni on 21/01/16.
 */
public class ConnectionHandler {

    public static JSONArray getJSONArrayAtUrl(String urlString) throws IOException {

        String s=null;
        JSONArray json=null;
        InputStream is = null;

        if(urlString!=null) {
            Log.e("URL REQ:", urlString);}
        else {return null;}


        try {

            URL url = null;
            try {
                url = new URL(urlString);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            HttpURLConnection conn = null;
            if (url != null) {
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            try {
                conn.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();

            is = conn.getInputStream();

            // Read response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                s = sb.toString();
            } catch(Exception e) {
                return null;
            }


            try {
                json = new JSONArray(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        finally {
            if(is!=null){
                is.close();}
        }
        return json;
    }


    public static boolean sendMessageToChat(String urlString) throws IOException{
        String s = null;
        JSONArray json = null;
        InputStream is = null;

        if (urlString == null) {
            return false;
        }

        try {

            URL url = null;
            try {
                url = new URL(urlString);
                Log.e("URL REQ:", url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            HttpURLConnection conn = null;
            if (url != null) {
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            try {
                conn.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();

            is = conn.getInputStream();

            // Read response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                s = sb.toString();
            } catch (Exception e) {
                return false;
            }
        }
        finally {
            if(is!=null){
                is.close();}
        }
        return true;
    }


    public static String sanitizeUrlString(String s) {
        s=s.replace("(", "");
        s=s.replace(")", "");
        s=s.replace("[", "");
        s=s.replace("]", "");
        s=s.replace("&", "and");
        s=s.replace("%", "perc");
        s=s.replace("$", "dollar");
        s=s.replace(" ", "%20");
        return s;
    }




}
