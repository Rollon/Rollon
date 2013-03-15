package com.rollonapp.rollon.activities;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rollonapp.rollon.R;
import com.rollonapp.rollon.feeds.Feed;
import com.rollonapp.rollon.feeds.FeedRepository;

public class FeedsActivity extends Activity {

	private ListView feedsListView;

	private final String RSS_FEED_SETTINGS = "RSS_FEED";
	private final String SYSTEM_SETTINGS = "SYSTEM";

	private Feed feeds[];
	private FeedListAdapter feedListAdapter;

	private ImageButton addFeedButton;
	private Activity activity;
	private TextView savedSoFarTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeds);

		// Populate the feed list to display on the menu screen.
		FeedRepository repo = new FeedRepository(this);
		List<Feed> repoFeeds = repo.getFeeds();
	
		feeds = repoFeeds.toArray(new Feed[0]);
		

		// Update the time shown.
		savedSoFarTime = (TextView) findViewById(R.id.savedSoFarTime);
		SharedPreferences systemSettings = getSharedPreferences(SYSTEM_SETTINGS, 0);
		int tempTime = systemSettings.getInt("TIME_SAVED", -1);
		savedSoFarTime.setText(tempTime + " minutes");

		feedsListView = (ListView) findViewById(R.id.list);

		feedListAdapter = new FeedListAdapter();

		feedsListView.setAdapter(feedListAdapter);

		activity = this;

		feedsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Feed selected = feeds[position];
				Intent i  = new Intent(activity, RssFeedReaderActivity.class);
				i.setData(Uri.parse(selected.getUrl().toString()));
				i.putExtra("FEED_NAME", selected.getName());
				startActivity(i);
			}

		});
	}

	@Override
	public void onRestart()
	{  
		// After a pause OR at startup
		super.onRestart();

		// Reinitialize the activity to recreate the menu 
		// and reload the time spent listening
		onCreate(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feeds, menu);

		// Adds the ability to create custom RSS sources
		addFeedButton = (ImageButton) menu.findItem(R.id.addFeedButton).getActionView();
		addFeedButton.setBackgroundResource(R.drawable.ic_menu_add);       

		addFeedButton.setOnClickListener(new View.OnClickListener() {

			// Allows the user to navigate to the custom RSS page when the button is clicked.
			@Override
			public void onClick(View v) {
				Intent i  = new Intent(activity, CustomRssActivity.class);
				startActivity(i);
			}
		});

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
