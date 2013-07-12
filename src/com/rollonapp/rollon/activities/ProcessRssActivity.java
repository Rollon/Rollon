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
import com.rollonapp.rollon.api.RollonApi;

public class ProcessRssActivity extends Activity {
    
    protected static final String TAG = "rollon";
    
    public static final String INTENT_EXTRA_FEED_NAME = "com.rollonapp.rollon.FeedName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_rss);

        Intent callingIntent = getIntent();

        String url = callingIntent.getDataString();
        String feedName = callingIntent.getStringExtra(INTENT_EXTRA_FEED_NAME);
        
        Log.i(TAG, "Got data and feed name: (" + url + ", " + feedName + ")");
        
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
            return RollonApi.getFeedArticles(url);
        }

        @Override
        protected void onPostExecute(List<RssItem> result) {
            if (result != null) {
                // TODO: Create RssReaderActivity and Start a new instance
                // passing in the data collected here.
                ArrayList<String> texts = new ArrayList<String>();
                ArrayList<String> subtitles = new ArrayList<String>();
                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> urls = new ArrayList<String>();
                
                for (RssItem item: result) {
                    String content = item.getContent();
                    String url = "";
                    if (content == null) {
                        content = "";
                        url = item.getLink();
                    }
                    
                    texts.add(content);
                    subtitles.add(item.getTitle());
                    titles.add(feedName);
                    urls.add(url);
                }
                
                Intent i = new Intent(ProcessRssActivity.this, RssReaderActivity.class);
                i.putStringArrayListExtra(ReaderActivity.INTENT_EXTRA_TITLES, titles);
                i.putStringArrayListExtra(ReaderActivity.INTENT_EXTRA_SUBTITLES, subtitles);
                i.putStringArrayListExtra(ReaderActivity.INTENT_EXTRA_TEXTS, texts);
                i.putStringArrayListExtra(RssReaderActivity.INTENT_EXTRA_LOOKUP_ARTICLE_URL, urls);
                
                startActivity(i);
                
            } else {
                Toast.makeText(ProcessRssActivity.this, "Error processing RSS Feed.", Toast.LENGTH_LONG).show();
                ProcessRssActivity.this.finish();
            }
        }
    }

}
