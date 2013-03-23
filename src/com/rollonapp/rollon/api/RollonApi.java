package com.rollonapp.rollon.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import android.util.Log;

public class RollonApi {
    
    private RollonApi() {}
    
    public static final String ROLLONAPP_API_ROOT = "http://rollonapp.com/article/";
    private static final String TAG = "rollon";

    /**
     * Uses the Rollon API to get the readable text from the given article URL.
     * @param articleUrl The URL of the article to get text for.
     * @return The readable text.
     */
    public static String getReadableArticleText(String articleUrl) {
        String text = "";
        
        try {
            String argument = URLEncoder.encode(articleUrl, "UTF-8");
            URL apiUrl = new URL(ROLLONAPP_API_ROOT + argument);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            InputStream in  = new BufferedInputStream(conn.getInputStream());
            Scanner s = new Scanner(in).useDelimiter("\\A");
            text = s.next();
        } catch (IOException e) {
            Log.e(TAG, "Could not get article text.", e);
        }
        
        return text;
    }
}
