package com.rollonapp.rollon;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

		// Read the file contents into a map for easier list management
		Map<String,String> feedMap = new HashMap<String, String>();
		try {
			InputStream rollonDataFile = getResources().openRawResource(R.raw.rollon_data);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(rollonDataFile);
			doc.getDocumentElement().normalize();

			// Get the list of RSSFeed elements
			NodeList nodes = doc.getElementsByTagName("RSSFeed");

			// Parse the feed title and url, and then add each to the map as key and value, respectively.
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String feedTitle = getValue("FeedTitle", element);
					String feedURL = getValue("FeedURL", element);
					feedMap.put(feedTitle, feedURL);
				}
			}
		} catch (Exception e){
			Log.e("rollon", "XML Parsing Error",e);
		}

		// Populate the feed list to display on the menu screen.
		feeds = new Feed[feedMap.size()];
		try {
			// Iterate through the mapp and add the entries to the feed list.
			int pos = 0;
			for (Entry<String, String> feedPair: feedMap.entrySet()){
				feeds[pos] = new Feed(feedPair.getKey(), new URL(feedPair.getValue()));
				pos++;
			}

		} catch (MalformedURLException e) {
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

	/*
	 * Helper function used to strip the specific node value from each element.
	 */
	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
}
