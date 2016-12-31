package com.neverwasradio.neverwasplayer.Core.metadata;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Giovanni on 19/02/16.
 */
class RetrieverHandler extends Handler
{
    public static final int ACTION_METADATA = 0;
    public static final int ACTION_HEADERS = 1;

    private WeakReference<OnNewMetadataListener> ref;
    private String mUrlString;

    public RetrieverHandler(String uri, OnNewMetadataListener listener)
    {
        mUrlString = uri;
        ref = new WeakReference<OnNewMetadataListener>(listener);
    }

    @Override
    public void handleMessage(Message msg)
    {
        super.handleMessage(msg);

        OnNewMetadataListener listener = ref.get();
        if (listener == null)
        {
            return;
        }
        switch (msg.what)
        {
            case(ACTION_METADATA):
            {
                String streamTitle = (String) msg.obj;
                listener.onNewStreamTitle(mUrlString, streamTitle);
                break;
            }
            case(ACTION_HEADERS):
            {
                Bundle data = msg.getData();
                List<String> name = data.getStringArrayList(ShoutcastHeader.NAME);
                List<String> desc = data.getStringArrayList(ShoutcastHeader.DESC);
                List<String> br = data.getStringArrayList(ShoutcastHeader.BR);
                List<String> genre = data.getStringArrayList(ShoutcastHeader.GENRE);
                List<String> info = data.getStringArrayList(ShoutcastHeader.INFO);
                listener.onNewHeaders(mUrlString, name, desc, br, genre, info);
                break;
            }
        }
    }
}
