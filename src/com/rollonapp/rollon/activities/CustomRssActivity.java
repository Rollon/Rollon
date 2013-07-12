package com.rollonapp.rollon.activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.rollonapp.rollon.R;
import com.rollonapp.rollon.api.RollonApi;
import com.rollonapp.rollon.feeds.Feed;
import com.rollonapp.rollon.feeds.FeedRepository;

public class CustomRssActivity extends Activity implements OnClickListener {

    protected Button previewButton, saveButton;
    protected ListView previewList;
    protected EditText feedUrlInput;
    protected ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_rss);

        previewButton = (Button) findViewById(R.id.previewFeedButton);
        saveButton = (Button) findViewById(R.id.saveFeedButton);
        previewList = (ListView) findViewById(R.id.previewList);
        feedUrlInput = (EditText) findViewById(R.id.feedUrl);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        previewList.setAdapter(adapter);
        previewButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custom_rss, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        final Activity activity = this;
        switch (v.getId()) {
            case R.id.previewFeedButton:

                new AsyncTask<String, Integer, List<String>>() {
                    @Override
                    protected List<String> doInBackground(String... params) {
                        List<RssItem> items = RollonApi.getFeedArticles(feedUrlInput.getText().toString());
                        List<String> titles = new ArrayList<String>();
                        if (items.size() > 0) {
                            for (RssItem item : items) {
                                titles.add(item.getTitle());
                            }
                        }

                        return titles;
                    }

                    protected void onPostExecute(List<String> titles) {
                        if (titles.size() > 0) {
                            adapter.clear();
                            adapter.addAll(titles);
                        } else {
                            Toast.makeText(activity, "Could not retireve RSS feed from the given url.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();

            break;
            case R.id.saveFeedButton:
                new AsyncTask<String, Integer, Boolean>() {

                    @Override
                    protected Boolean doInBackground(String... params) {
                        String url = feedUrlInput.getText().toString();
                        RssFeed rssFeed = RollonApi.getFeed(url);
                        List<RssItem> items = rssFeed.getRssItems();
                        if (items != null && items.size() > 0) {
                            // Add it, it appears to be a valid RSS feed
                            FeedRepository repo = FeedRepository.getInstance(activity);
                            List<Feed> feeds = repo.getFeeds();
                            try {
                                feeds.add(new Feed(rssFeed.getTitle(), new URL(url)));
                                repo.setFeeds(feeds);
                            } catch (MalformedURLException e) {
                                return false;
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            Toast.makeText(activity, "Feed added successfully", Toast.LENGTH_LONG).show();
                            activity.finish();
                        } else {
                            Toast.makeText(activity, "Could not add feed, please check URL and try again", Toast.LENGTH_LONG).show();
                        }
                    }

                }.execute();
            break;
        }

    }

}
