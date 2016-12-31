package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neverwasradio.neverwasplayer.Core.PlayerService;
import com.neverwasradio.neverwasplayer.R;
import com.neverwasradio.neverwasplayer.UI.CustomView.NWBigButton;


public class PlayerFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private static PlayerFragment instance;

    private static NWBigButton buttonPlayer;


    public static PlayerFragment getInstance() {
        if(instance==null) { instance=new PlayerFragment();}
        return instance;
    }

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        buttonPlayer = (NWBigButton) rootView.findViewById(R.id.mainPlayerButton);


        initSectionButtons();
        initListeners();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initSectionButtons(){
        buttonPlayer.setTitle("Ascolta");
        buttonPlayer.setIcon(R.drawable.play_bn);
    }

    private void initListeners(){
        buttonPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (MainActivity.playerLock) {
                    return;
                }

                if (!MainActivity.isNetworkConnected(getActivity())) {
                    updatePlayerIcon(MainActivity.PlayState.NETWORK_ERROR);
                    return;
                }

                if (!PlayerService.isActive()) {

                    if (PlayerService.isReady()) {
                        updatePlayerIcon(MainActivity.PlayState.PLAY);
                        // avvia
                        PlayerService.setActive(true);
                        PlayerService.startActionStart(getActivity());

                    } else if (PlayerService.isLoading()) {
                        updatePlayerIcon(MainActivity.PlayState.PLAY);
                        PlayerService.setActive(true);
                    } else {
                        MainActivity.playerLock = true;
                        updatePlayerIcon(MainActivity.PlayState.BUFFERING);
                        // inizializza
                        PlayerService.setActive(true);

                        if (MainActivity.getPreloadingStatus() == null || MainActivity.getPreloadingStatus().compareTo(AsyncTask.Status.FINISHED) == 0) {
                            MainActivity.executePreloding();
                        }
                    }

                } else {
                    updatePlayerIcon(MainActivity.PlayState.STOP);

                    //stoppa
                    PlayerService.startActionStop(getActivity());
                }

                return;
            }
        });

    }



    public static void updatePlayerIcon(MainActivity.PlayState playState) {
        if(buttonPlayer==null) {return;}
        switch (playState) {
            case PLAY:
                buttonPlayer.setIcon(R.drawable.pause_bn);
                buttonPlayer.animateRotation(false);
                buttonPlayer.setTitle("premi per fermare");
                break;

            case STOP:
                buttonPlayer.setIcon(R.drawable.play_bn);
                buttonPlayer.animateRotation(false);
                buttonPlayer.setTitle("Ascolta");
                break;

            case BUFFERING:
                buttonPlayer.setIcon(R.drawable.loading_bn);
                buttonPlayer.setTitle("carico dati....");
                buttonPlayer.animateRotation(true);
                break;

            case ERROR:
                buttonPlayer.setIcon(R.drawable.error_bn);
                buttonPlayer.animateRotation(false);
                buttonPlayer.setTitle("offline, stiamo risolvendo!");
                break;

            case NETWORK_ERROR:
                buttonPlayer.setIcon(R.drawable.play_bn);
                buttonPlayer.animateRotation(false);
                buttonPlayer.setTitle("non sei connesso alla rete");
                break;
        }

        buttonPlayer.invalidate();
    }
}
