package com.rollonapp.rollon.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

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
    
    /**
     * Given a url to an RSS feed, returns a list of the entries in it.
     * @param url The URL to the feed.
     * @return The entries in the feed.
     */
    public static List<RssItem> getFeedArticles(String url) {
        RssFeed feed;
        List<RssItem> items = new ArrayList<RssItem>();
        try {
            feed = RssReader.read(new URL(url));
            items = feed.getRssItems();
        } catch (MalformedURLException e) {
           Log.e(TAG, "Bad URL", e);
        } catch (SAXException e) {
            Log.e(TAG, "Bad Rss Feed", e);
        } catch (IOException e) {
            Log.e(TAG, "Something Bad Happened", e);
        }

        return items;
    }
    
    /**
     * Returns the full RssFeed object for the given URL.
     * @param url The URL of the feed.
     * @return The RssFeed object
     */
    public static RssFeed getFeed(String url) {
        RssFeed feed = null;
        try {
            feed = RssReader.read(new URL(url));
        } catch (MalformedURLException e) {
           Log.e(TAG, "Bad URL", e);
        } catch (SAXException e) {
            Log.e(TAG, "Bad Rss Feed", e);
        } catch (IOException e) {
            Log.e(TAG, "Something Bad Happened", e);
        }

        return feed;
    }
}
