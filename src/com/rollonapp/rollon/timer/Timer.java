package com.rollonapp.rollon.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;

/**
 * Used to keep track of time saved, meaning while the app is reading content to
 * the user.
 * 
 */
public class Timer {

    public static final String SETTINGS_TIME_SAVED = "TIME_SAVED";
    public static final String SYSTEM_SETTINGS = "SYSTEM";

    protected Time startTime;
    protected SharedPreferences systemSettings;

    public Timer(Context context) {
        startTime = new Time();
        startTime.setToNow();
        
        systemSettings = context.getSharedPreferences(SYSTEM_SETTINGS, Context.MODE_PRIVATE);
    }

    /**
     * Starts the timer
     */
    public void start() {
        startTime.setToNow();
    }

    /**
     * Stops the timer and saves the time elapsed to settings file.
     */
    public void stop() {
        Time endTime = new Time();
        endTime.setToNow();
        
        // Calculate the time elapsed and update
        int timeElapsed =  (int) ((endTime.toMillis(true) - startTime.toMillis(true)) / 100000L);
        int totalTime = systemSettings.getInt(SETTINGS_TIME_SAVED, 0);
        totalTime += timeElapsed;
        
        SharedPreferences.Editor editor = systemSettings.edit();
        editor.putInt(SETTINGS_TIME_SAVED, totalTime);
        editor.commit();
    }
}
