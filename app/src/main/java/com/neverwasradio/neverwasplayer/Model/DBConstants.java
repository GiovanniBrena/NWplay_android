package com.neverwasradio.neverwasplayer.Model;

/**
 * Created by Giovanni on 15/03/16.
 */
public interface DBConstants {

    public static final String DBNAME="NeverwasPlayDB";

    public interface ProgramsTable{
        public static final String TABLE_NAME="NWProgramsTable";
        public static final String PROGRAM_ID="programId";
        public static final String TITLE="title";
        public static final String DESC="desc";
        public static final String DAY="day";
        public static final String HOUR="hour";
        public static final String DURATION="duration";
        public static final String SITE_URL="siteUrl";
        public static final String ICON_URL="iconUrl";
        public static final String ICON_BITMAP="iconBitmap";
        public static final String FB_ID="fbId";
    }

    public interface PostTable{
        public static final String TABLE_NAME="NWPostTable";
        public static final String ID="id";
        public static final String FB_ID="fbId";
        public static final String TEXT="text";
        public static final String DATE="date";
        public static final String INSERT_DATE="insertDate";
        public static final String TAG="tag";
        public static final String IMG_URL="imgUrl";
        public static final String ICON_BITMAP="iconBitmap";
    }

}
