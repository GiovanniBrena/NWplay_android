package com.neverwasradio.neverwasplayer.Core.metadata;

import java.util.List;

/**
 * Created by Giovanni on 19/02/16.
 */
public interface OnNewMetadataListener
{
    void onNewHeaders(String stringUri, List<String> name, List<String> desc, List<String> br,
                      List<String> genre, List<String> info);
    void onNewStreamTitle(String stringUri, String streamTitle);
}

