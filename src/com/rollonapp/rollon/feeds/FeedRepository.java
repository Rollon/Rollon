package com.rollonapp.rollon.feeds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class FeedRepository {

    private static final String FEEDS_FILE = "RollonFeeds.dat";

    private Context context;
    private List<Feed> feeds;

    public FeedRepository(Context context) {
        this.context = context;
        feeds = readFromFile();
    }

    public List<Feed> getFeeds() {
        return new ArrayList<Feed>(feeds);
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = new ArrayList<Feed>(feeds);
        
        writeToFile(this.feeds);
    }

    private void writeToFile(List<Feed> feeds) {
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(context.openFileOutput(FEEDS_FILE, Context.MODE_PRIVATE));
            os.writeObject(feeds);
            os.close();
        } catch (FileNotFoundException e) {
            Log.e("rollon", "File not found while writing file", e);
        } catch (IOException e) {
            Log.e("rollon", "IOException while writing file", e);
        }

    }

    private List<Feed> readFromFile() {
        List<Feed> toReturn = new ArrayList<Feed>();

        ObjectInputStream oi;
        try {
            
            oi = new ObjectInputStream(context.openFileInput(FEEDS_FILE));

            toReturn = (List<Feed>) oi.readObject();
            oi.close();

        } catch (FileNotFoundException e) {
            Log.i("rollon", "Could not find file, returning empty list.");
        } catch (IOException e) {
            Log.e("rollon", "IOExcpetion while reading file.", e);
        } catch (ClassNotFoundException e) {
            Log.e("rollon", "ClassNotFoundException while reading feeds.", e);
        }

        return toReturn;
    }

}
