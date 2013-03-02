package com.rollonapp.rollon;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FeedsActivity extends Activity {
    
    private ListView feedsListView;
    
    private Feed feeds[];
    private FeedListAdapter feedListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
         
        feeds = new Feed[2];
        try {
            feeds[0] = new Feed("TechCrunch", new URL("http://feeds.feedburner.com/TechCrunch/"));
            feeds[1] = new Feed("Engadget", new URL("http://www.engadget.com/rss.xml"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            Log.e("rollon", "Bad URL",  e);
        }
        
        feedsListView = (ListView) findViewById(R.id.list);
        
        feedListAdapter = new FeedListAdapter();
        
        feedsListView.setAdapter(feedListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feeds, menu);
        return true;
    }
    
    private class FeedListAdapter extends ArrayAdapter<Feed> {
        FeedListAdapter() {
            super(FeedsActivity.this, R.layout.feed_item,R.id.text,feeds);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          View v = super.getView(position, convertView, parent);

          TextView text = (TextView) v.findViewById(R.id.text);

          text.setText(feeds[position].getName());
          return v;
        }
    }

}
