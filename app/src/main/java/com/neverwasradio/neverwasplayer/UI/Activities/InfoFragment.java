package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.neverwasradio.neverwasplayer.Core.ConnectionHandler;
import com.neverwasradio.neverwasplayer.R;
import com.neverwasradio.neverwasplayer.UI.CustomView.NWBigButton;

import java.io.IOException;


/**
 * Created by chiara on 13/08/15.
 */
public class InfoFragment extends Fragment {
    private static InfoFragment instance;

    ImageView sectionTime;
    ImageView sectionNews;
    ImageView sectionChat;
    ImageView sectionSocial;
    ImageView sectionOption;


    public static InfoFragment getInstance() {
        if(instance==null) {instance=new InfoFragment();}
        return instance;
    }
    public InfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        sectionTime = (ImageView) rootView.findViewById(R.id.sectionTimetable);
        sectionNews = (ImageView) rootView.findViewById(R.id.sectionNews);
        sectionChat = (ImageView) rootView.findViewById(R.id.sectionChat);
        sectionSocial = (ImageView) rootView.findViewById(R.id.sectionSocial);
        sectionOption = (ImageView) rootView.findViewById(R.id.sectionOption);

        initListeners();

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initListeners(){

        sectionChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), ChatActivity.class);
                startActivityForResult(myIntent, 300);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
            }
        });

        sectionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), ProgramsActivity.class);
                startActivityForResult(myIntent, 300);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
            }
        });

        sectionNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), NewsActivity.class);
                startActivityForResult(myIntent, 300);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
            }
        });

        sectionSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), SocialActivity.class);
                //startActivity(myIntent);
                startActivityForResult(myIntent, 300);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
            }
        });

        sectionOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), ExtraActivity.class);
                startActivityForResult(myIntent, 300);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
            }
        });

    }

}
