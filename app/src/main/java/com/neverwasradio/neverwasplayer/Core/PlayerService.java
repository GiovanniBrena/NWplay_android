package com.neverwasradio.neverwasplayer.Core;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.neverwasradio.neverwasplayer.Core.metadata.AudiostreamMetadataManager;
import com.neverwasradio.neverwasplayer.Core.metadata.OnNewMetadataListener;
import com.neverwasradio.neverwasplayer.Core.metadata.UserAgent;
import com.neverwasradio.neverwasplayer.UI.Activities.MainActivity;
import com.neverwasradio.neverwasplayer.UI.Activities.PlayerFragment;


import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlayerService extends IntentService {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";

    private static boolean active;
    private static boolean ready;
    private static boolean loading;
    private static boolean error;
    private static boolean fistRun =true;
    private static MediaPlayer player;
    private static MediaPlayer.TrackInfo trackInfo[];
    public static final String RADIO_URL_SOURCE = "http://www.associazionesmart.it/cgi-bin/picoreader.cgi?user=neverwas&r=1362900603&f=file.mp3";

    public static boolean isActive() {return active;}
    public static boolean isReady() {return ready;}
    public static boolean isLoading() {return loading;}
    public static boolean isFistRun(){return fistRun;}

    public static void setActive(boolean value) {active=value;}
    public static boolean isError() {
        return error;
    }


    public static void startActionStop(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }


    public static void startActionStart(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public PlayerService() {
        super("PlayerService");
    }


    // CHOOSE ACTION TODO
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_STOP.equals(action)) {
                handleActionStop();

            } else if (ACTION_START.equals(action)) {
                handleActionStart();
            }
        }
    }

    // STOP ACTIONS
    private void handleActionStop() {
        active=false;
        ready=false;
        loading=false;

        player.stop();
        player.reset();
    }

    // START ACTIONS
    private void handleActionStart() {
        active=true;

        if(ready) {
            loading=false;
            player.start();
        }

        else if(!loading){
            try {
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initMetaDataRetriever() {

        Uri streamUri = Uri.parse(RADIO_URL_SOURCE);

        OnNewMetadataListener listener = new OnNewMetadataListener()
        {
            @Override
            public void onNewHeaders(String stringUri, List<String> name, List<String> desc,
                                     List<String> br, List<String> genre, List<String> info) {
                for(int i=0; i<name.size(); i++) { Log.e("NAME", name.get(i)); }
                for(int i=0; i<desc.size(); i++) { Log.e("DESC", desc.get(i)); }
                for(int i=0; i<info.size(); i++) { Log.e("INFO", info.get(i)); }
                for(int i=0; i<br.size(); i++) { Log.e("BR", br.get(i)); }
                for(int i=0; i<genre.size(); i++) { Log.e("GENRE", genre.get(i)); }
            }

            @Override
            public void onNewStreamTitle(String stringUri, String streamTitle) {
                Log.e("STREAM-TITLE", streamTitle);
            }
        };

        //Start parsing
        AudiostreamMetadataManager.getInstance()
                .setUri(streamUri)
                .setOnNewMetadataListener(listener)
                .setUserAgent(UserAgent.WINDOWS_MEDIA_PLAYER)
                .start();

    }

    public static void initializePlayer() {

        Log.e("STREAM AUDIO", "begin initialize");

        //initMetaDataRetriever();

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPrepared(MediaPlayer mp) {
                fistRun = false;
                loading = false;
                ready = true;
                if (active && !error) {
                    PlayerFragment.updatePlayerIcon(MainActivity.PlayState.PLAY);
                    player.start();

                    Log.e("STREAM AUDIO", "start playing");
                } else if(error) {
                    Log.e("STREAM AUDIO", "player stream error");
                }

                else {
                    Log.e("STREAM AUDIO", "player ready");
                }
            }
        });

        if(!loading) {
            try {
                player.setDataSource(RADIO_URL_SOURCE);
                player.prepare();
                loading = true;
            } catch (IOException e) {
                Log.e("STREAM AUDIO", "url error");
                error=true;
                e.printStackTrace();
                //playerFragment.showErrorConnection();
            }
        }


    }


    public static void resetPlayer(){
        if(player!=null) {
            Log.e("STREAM AUDIO", "reset");
            player.reset();
            active=false;
            ready=false;
            loading=false;
            error=false;
        }
    }



}

