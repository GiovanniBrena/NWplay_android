package com.neverwasradio.neverwasplayer.Core.metadata;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Giovanni on 19/02/16.
 */
public class AudiostreamMetadataRetriever implements Runnable
{
    public static final String TAG = AudiostreamMetadataRetriever.class.getSimpleName();

    private final Handler mHandler;
    private final String mUrlString;
    private final String mUserAgent;

    public AudiostreamMetadataRetriever(String urlString, OnNewMetadataListener listener)
    {
        this(urlString, listener, UserAgent.VLC.toString());
    }

    public AudiostreamMetadataRetriever(String urlString, OnNewMetadataListener listener, String userAgent)
    {
        if (TextUtils.isEmpty(urlString))
        {
            throw new IllegalArgumentException("Uri must be non-empty");
        }
        if (listener == null)
        {
            throw new IllegalArgumentException("You must set Callbacks");
        }
        if (TextUtils.isEmpty(userAgent))
        {
            throw new IllegalArgumentException("User-agent must be non-empty");
        }

        mUrlString = urlString;
        mUserAgent = userAgent;
        mHandler = new RetrieverHandler(mUrlString, listener);
    }

    @Override
    public void run()
    {
        URL url = null;
        try
        {
            url = new URL(mUrlString);
        }
        catch (MalformedURLException e)
        {
            Log.e(TAG, "URL is incorrect");
            e.printStackTrace();
            return;
        }

        HttpURLConnection urlConnection = null;
        try
        {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", mUserAgent);
            urlConnection.setRequestProperty("Icy-MetaData", "1");
            urlConnection.connect();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Failed to open connection");
            e.printStackTrace();
            return;
        }


        Map<String, List<String>> headers = urlConnection.getHeaderFields();
        Bundle headers_data = new Bundle();
        headers_data.putStringArrayList(ShoutcastHeader.NAME,
                headers.get(ShoutcastHeader.NAME) != null ? new ArrayList<String>(headers.get(ShoutcastHeader.NAME)): new ArrayList<String>());
        headers_data.putStringArrayList(ShoutcastHeader.DESC,
                headers.get(ShoutcastHeader.DESC) != null ? new ArrayList<String>(headers.get(ShoutcastHeader.DESC)): new ArrayList<String>());
        headers_data.putStringArrayList(ShoutcastHeader.BR,
                headers.get(ShoutcastHeader.BR) != null ? new ArrayList<String>(headers.get(ShoutcastHeader.BR)): new ArrayList<String>());
        headers_data.putStringArrayList(ShoutcastHeader.GENRE,
                headers.get(ShoutcastHeader.GENRE) != null ? new ArrayList<String>(headers.get(ShoutcastHeader.GENRE)): new ArrayList<String>());
        headers_data.putStringArrayList(ShoutcastHeader.INFO,
                headers.get(ShoutcastHeader.INFO) != null ? new ArrayList<String>(headers.get(ShoutcastHeader.INFO)): new ArrayList<String>());
        Message headers_msg = Message.obtain();
        headers_msg.what = RetrieverHandler.ACTION_HEADERS;
        headers_msg.setData(headers_data);
        mHandler.sendMessage(headers_msg);


        if (!headers.containsKey("icy-metaint"))
        {
            Log.i(TAG, "IceCast server doesn't support metadata");
            urlConnection.disconnect();
            return;
        }

        String icy_metaint = urlConnection.getHeaderField("icy-metaint");
        final int ICY_METAINT = Integer.parseInt(icy_metaint);

        InputStream stream = null;
        try
        {
            stream = urlConnection.getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "InputStream can not be created ");
            urlConnection.disconnect();
            return;
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            while (!Thread.interrupted())
            {
                long skipped = stream.skip(ICY_METAINT);
                while (skipped != ICY_METAINT)
                {
                    skipped += stream.skip(ICY_METAINT - skipped);
                }

                int symbolLength = stream.read();
                int metaDataLength = symbolLength * 16;
                if (metaDataLength > 0)
                {
                    for (int i = 0; i < metaDataLength; ++i)
                    {
                        int metaDataSymbol = stream.read();
                        if (metaDataSymbol > 0)
                        {
                            baos.write(metaDataSymbol);
                        }
                    }

                    String result = baos.toString()
                            .replace("StreamTitle=", "")
                            .replaceAll("'", "")
                            .replaceAll(";", "");
                    baos.reset();

                    Log.d(TAG, result);

                    Message msg = Message.obtain();
                    msg.what = RetrieverHandler.ACTION_METADATA;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "Failed to obtain metadata");
        }
        finally
        {
            try
            {
                baos.close();
                stream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        urlConnection.disconnect();
    }
}