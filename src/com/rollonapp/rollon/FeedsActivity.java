package com.rollonapp.rollon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
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

public class FeedsActivity extends Activity {

	private ListView feedsListView;

	private Feed feeds[];
	private FeedListAdapter feedListAdapter;

	private ImageButton addFeedButton;
	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeds);

		// Read the file contents through toast!
		BufferedReader input = null;
		Map<String,String> feedMap = new HashMap<String, String>();
		input = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.feed_list_file)));
		try {
			String line;
			while ((line = input.readLine()) != null) {
				String feedTitle = line.substring(0, line.indexOf("\t"));
				String feedUrl = line.substring(line.indexOf("\t")+1);
				feedMap.put(feedTitle, feedUrl);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		feeds = new Feed[feedMap.size()];
		try {
			int pos = 0;
			for (Entry<String, String> feedPair: feedMap.entrySet()){
				feeds[pos] = new Feed(feedPair.getKey(), new URL(feedPair.getValue()));
				pos++;
			}
			
			/*
			feeds[0] = new Feed("TechCrunch", new URL("http://feeds.feedburner.com/TechCrunch/"));
			feeds[1] = new Feed("Engadget", new URL("http://www.engadget.com/rss.xml"));
			feeds[2] = new Feed("Food.com", new URL("http://www.food.com/rss"));
			feeds[3] = new Feed("Columbus Dispatch", new URL("http://www.dispatch.com/content/syndication/news_national.xml"));
			feeds[4] = new Feed("CBS Sports", new URL("http://feeds.cbssports.com/cbssportsline/home_news"));
			feeds[5] = new Feed("Inc", new URL("http://feeds.inc.com/home/updates"));
			feeds[6] = new Feed("Entrepreneur", new URL("http://feeds.feedburner.com/entrepreneur/latest"));*/
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e("rollon", "Bad URL",  e);
		}

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
