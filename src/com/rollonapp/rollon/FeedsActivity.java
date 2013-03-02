package com.rollonapp.rollon;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;

public class FeedsActivity extends Activity {
    
    private DragSortListView feedsListView;
    
    private ArrayList<Feed> feeds = null;
    private FeedListAdapter feedListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        
        feeds = new ArrayList<Feed>();
        
        for (int i = 0; i < 10; i++) {
            Feed f = new Feed();
            f.setName("Frank " + i);
            feeds.add(f);
        }
        
        feedsListView = (DragSortListView) findViewById(R.id.list);
        
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

          text.setText(feeds.get(position).getName());
          return v;
        }
    }

}
