package com.neverwasradio.neverwasplayer.UI.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neverwasradio.neverwasplayer.R;

/**
 * Created by Giovanni on 02/03/16.
 */
public class NWBigButton extends RelativeLayout {

    private ImageView icon;
    private TextView title;

    RotateAnimation rotateAnimation;

    public NWBigButton(Context context) {
        super(context);
        init();
    }

    public NWBigButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NWBigButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setTitle(String text) {
        this.title.setText(text);
    }

    public void setIcon(int resId) {
        icon.setImageResource(resId);
    }

    private void init() {
        inflate(getContext(),R.layout.big_button_layout,this);

        icon = (ImageView) findViewById(R.id.bigBicon);
        title = (TextView) findViewById(R.id.bigBtitle);
    }

    public void setHorizontalAligned() {
        inflate(getContext(),R.layout.big_button_horizontal_layout,this);

        icon = (ImageView) findViewById(R.id.bigBHicon);
        title = (TextView) findViewById(R.id.bigBHtitle);
    }

    public void showTitle(boolean value) {
        if(value) {title.setVisibility(VISIBLE);}
        else { title.setVisibility(GONE);}
    }

    public void animateRotation(boolean value) {
        if(value) {
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(5000);
            rotateAnimation.setRepeatCount(-1);
            rotateAnimation.setInterpolator(new LinearInterpolator());

            icon.startAnimation(rotateAnimation);
        }

        else if(rotateAnimation!=null){
            rotateAnimation.cancel();
        }
    }

}
