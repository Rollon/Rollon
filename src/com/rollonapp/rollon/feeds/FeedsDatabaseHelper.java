package com.rollonapp.rollon.feeds;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedsDatabaseHelper extends SQLiteOpenHelper {
    
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Feeds.db";
    
    public FeedsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
