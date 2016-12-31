package com.neverwasradio.neverwasplayer.Model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.neverwasradio.neverwasplayer.Utilities.DBBitmapUtility;

import java.io.InputStream;
import java.util.BitSet;

/**
 * Created by Giovanni on 23/02/16.
 */
public class FbPost {

    private String text;
    private String date;
    private String id;
    private long insertDate;
    private String[] tag;
    private Bitmap img;
    private String imgUrl;

    private boolean hasImage;
    private boolean isLocal;

    public FbPost(String id, String text, String date, String imgUrl, String[] tag) {
        this.id=id;
        this.text=text;
        this.date=date;
        this.insertDate=System.currentTimeMillis();
        this.tag=tag;
        if(imgUrl!=null) {
            hasImage=true;
            this.imgUrl=imgUrl;
        }
        this.isLocal=false;
    }

    public FbPost(Cursor c){

        this.id = c.getString(c.getColumnIndex(DBConstants.PostTable.FB_ID));
        this.text = c.getString(c.getColumnIndex(DBConstants.PostTable.TEXT));
        this.date = c.getString(c.getColumnIndex(DBConstants.PostTable.DATE));
        this.imgUrl = c.getString(c.getColumnIndex(DBConstants.PostTable.IMG_URL));
        this.img = DBBitmapUtility.getImage(c.getBlob(c.getColumnIndex(DBConstants.PostTable.ICON_BITMAP)));
        String tags = c.getString(c.getColumnIndex(DBConstants.PostTable.TAG));
        if(tags!=null) {
            this.tag = c.getString(c.getColumnIndex(DBConstants.PostTable.TAG)).split("%");
        }
        this.insertDate= c.getLong(c.getColumnIndex(DBConstants.PostTable.INSERT_DATE));

        if(this.img!=null) {this.hasImage=true;}
        this.isLocal=true;

    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImg(){
        return img;
    }

    public long getInsertDate() {return insertDate;}

    public String[] getTag() {
        return tag;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public boolean isLocal() {
        return isLocal;
    }

}
