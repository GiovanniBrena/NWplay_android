package com.neverwasradio.neverwasplayer.UI.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import com.neverwasradio.neverwasplayer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO: document your custom view class.
 */
public class NWPlayerButton extends View {

    Activity mainActivity;

    public final static int PLAY = 0;
    public final static int PAUSE = 1;
    public final static int BLANK = 2;

    private final static int LOAD_ANIM_TIME = 800;

    private Paint paint;
    private int width, height, radius;

    private int mode;
    private int loadingStatus=0;

    private Timer loadingTimer;
    LoadingViewTimerTask loadingViewTimerTask;

    public NWPlayerButton(Context context) {
        super(context);
        init(null, 0);
    }

    public NWPlayerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NWPlayerButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        paint=new Paint();
        mode = PLAY;
    }

    public void setMainActivity(Activity a) {mainActivity=a;}

    public void setMode(int m) {
        mode=m;
        /*
        if(m==2) {
            loadingTimer=new Timer();
            loadingStatus=0;
            loadingViewTimerTask=new LoadingViewTimerTask();
            loadingTimer.schedule(loadingViewTimerTask,LOAD_ANIM_TIME);
        }
        */
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width=this.getWidth();
        height=this.getHeight();

        // disegno cerchio sfondo
        paint.setColor(Color.BLACK);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        radius=Math.min(width,height)/2-8;
        canvas.drawCircle(width/2,height/2,radius,paint);


        // disegno contenuto
        paint.setColor(Color.WHITE);

        if(mode==PLAY) {
            Point point1_draw = new Point();
            Point point2_draw = new Point();
            Point point3_draw = new Point();

            int xMargin = radius/6;
            int yMargin = radius/6;

            point1_draw.set(width/2-xMargin*2,height/2+yMargin*3);
            point2_draw.set(width/2-xMargin*2,height/2-yMargin*3);
            point3_draw.set(width/2+xMargin*3,height/2);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(point1_draw.x,point1_draw.y);
            path.lineTo(point2_draw.x,point2_draw.y);
            path.lineTo(point3_draw.x,point3_draw.y);
            path.lineTo(point1_draw.x,point1_draw.y);
            path.close();

            canvas.drawPath(path, paint);
        }

        else if(mode==PAUSE) {
            int xMargin = radius/6;
            int yMargin = radius/6;

            canvas.drawRect(width/2-xMargin*3,height/2-yMargin*3,width/2-xMargin,height/2+yMargin*3,paint);
            canvas.drawRect(width/2+xMargin,height/2-yMargin*3,width/2+xMargin*3,height/2+yMargin*3,paint);

        }

        else if(mode==BLANK) {
            /*
            int offset=0;
            int rectSize = radius/4;

            if(loadingStatus==0) {offset=-rectSize*2;}
            else if(loadingStatus==1) {offset=0;}
            else if(loadingStatus==2) {offset=rectSize*2;}

            canvas.drawRect(width/2-rectSize/2+offset, height/2-rectSize/2, width/2+rectSize/2+offset, height/2+rectSize/2, paint);
        */
        }



    }


    private class LoadingViewTimerTask extends TimerTask {

        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mode==BLANK) {
                        loadingStatus++;
                        if (loadingStatus > 2) {
                            loadingStatus = 0;
                        }
                        invalidate();
                        loadingViewTimerTask=new LoadingViewTimerTask();
                        loadingTimer.schedule(loadingViewTimerTask,LOAD_ANIM_TIME);
                    }
                }
            });
        }
    }

}