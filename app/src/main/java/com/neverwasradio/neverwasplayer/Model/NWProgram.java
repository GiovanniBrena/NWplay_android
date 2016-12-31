package com.neverwasradio.neverwasplayer.Model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.neverwasradio.neverwasplayer.Utilities.DBBitmapUtility;

import org.w3c.dom.NamedNodeMap;

import java.io.InputStream;

public class NWProgram {
    private String id;
    private String name;
    private String desc;
    private int day;
    private String hour;
    private int duration;
    private Bitmap icon;
    private String iconUrl;
    private String siteUrl;
    private String fbId;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public int getDuration() {
        return duration;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getFbId() { return fbId; }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public NWProgram(org.w3c.dom.Node n) {

        NamedNodeMap nodeMap = n.getAttributes();
        this.id=nodeMap.getNamedItem("id").getTextContent();
        this.name=nodeMap.getNamedItem("name").getTextContent();
        this.desc=nodeMap.getNamedItem("desc").getTextContent();
        this.day= Integer.parseInt(nodeMap.getNamedItem("day").getTextContent());
        this.hour=nodeMap.getNamedItem("hour").getTextContent();
        this.duration=Integer.parseInt(nodeMap.getNamedItem("duration").getTextContent());
        this.siteUrl=nodeMap.getNamedItem("link").getTextContent();
        this.fbId=nodeMap.getNamedItem("fbId").getTextContent();
        this.iconUrl=nodeMap.getNamedItem("icon").getTextContent();

        try {
            InputStream in = new java.net.URL(nodeMap.getNamedItem("icon").getTextContent()).openStream();
            this.icon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public NWProgram(Cursor c){

        this.id = c.getString(c.getColumnIndex(DBConstants.ProgramsTable.PROGRAM_ID));
        this.name = c.getString(c.getColumnIndex(DBConstants.ProgramsTable.TITLE));
        this.desc = c.getString(c.getColumnIndex(DBConstants.ProgramsTable.DESC));
        this.day = c.getInt(c.getColumnIndex(DBConstants.ProgramsTable.DAY));
        this.duration = c.getInt(c.getColumnIndex(DBConstants.ProgramsTable.DURATION));
        this.hour = c.getString(c.getColumnIndex(DBConstants.ProgramsTable.HOUR));
        this.iconUrl = c.getString(c.getColumnIndex(DBConstants.ProgramsTable.ICON_URL));
        this.icon = DBBitmapUtility.getImage(c.getBlob(c.getColumnIndex(DBConstants.ProgramsTable.ICON_BITMAP)));
        this.siteUrl = c.getString(c.getColumnIndex(DBConstants.ProgramsTable.SITE_URL));
        this.fbId = c.getString(c.getColumnIndex(DBConstants.ProgramsTable.FB_ID));

    }

    public String toString() {
        return "Name: "+name+" id: "+id+" desc: "+desc+" day: "+day+" hour: " +
                " duration: "+duration +" link: "+siteUrl;
    }

}
