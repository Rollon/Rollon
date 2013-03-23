package com.rollonapp.rollon.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.rollonapp.rollon.R;

public class ProcessRssActivity extends Activity {
    
    protected static final String TAG = "rollon";
    
    public static final String INTENT_EXTRA_RSS_URL = "com.rollonapp.rollon.RssUrl";
    public static final String INTENT_EXTRA_FEED_NAME = "com.rollonapp.rollon.FeedName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_rss);

        Intent callingIntent = getIntent();

        String url = callingIntent.getStringExtra(INTENT_EXTRA_RSS_URL);
        String feedName = callingIntent.getStringExtra(INTENT_EXTRA_FEED_NAME);

        new GetRSSFeedTask(url, feedName).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.process_rss, menu);
        return true;
    }

    private class GetRSSFeedTask extends AsyncTask<Void, Integer, List<RssItem>> {
        private String url, feedName;

        public GetRSSFeedTask(String url, String feedName) {
            super();
            this.url = url;
            this.feedName = feedName;
        }

        @Override
        protected List<RssItem> doInBackground(Void... params) {
            // TODO: Parse RSS Feed URL for articles
            RssFeed feed;
            ArrayList<RssItem> items = null;
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

        @Override
        protected void onPostExecute(List<RssItem> result) {
            if (result != null) {
                // TODO: Create RssReaderActivity and Start a new instance
                // passing in the data collected here.
                ArrayList<String> texts = new ArrayList<String>();
                ArrayList<String> subtitles = new ArrayList<String>();
                ArrayList<String> titles = new ArrayList<String>();
                
                for (RssItem item: result) {
                    String content = item.getContent();
                    if (content == null) {
                        content = "";
                    }
                    
                    texts.add(content);
                    subtitles.add("This is a subtitle");
                    titles.add(feedName);
                }
                
                Intent i = new Intent(ProcessRssActivity.this, ReaderActivity.class);
                i.putStringArrayListExtra(ReaderActivity.INTENT_EXTRA_TITLES, titles);
                i.putStringArrayListExtra(ReaderActivity.INTENT_EXTRA_SUBTITLES, subtitles);
                i.putStringArrayListExtra(ReaderActivity.INTENT_EXTRA_TEXTS, texts);
                startActivity(i);
                
            } else {
                Toast.makeText(ProcessRssActivity.this, "Error processing RSS Feed.", Toast.LENGTH_LONG).show();
                ProcessRssActivity.this.finish();
            }
        }
    }

}
