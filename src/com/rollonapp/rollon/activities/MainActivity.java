package com.rollonapp.rollon.activities;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.rollonapp.rollon.R;
import com.rollonapp.rollon.feeds.Feed;
import com.rollonapp.rollon.feeds.FeedRepository;

public class MainActivity extends Activity {

	private final String SYSTEM_SETTINGS = "SYSTEM";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        
        
        // Check if the preferences have been initialized, if not, add the proper values.
        SharedPreferences systemSettings = getSharedPreferences(SYSTEM_SETTINGS, 0);
        Boolean initialRun = systemSettings.getBoolean("FIRST_RUN", true);
        if ( initialRun ){
        	SharedPreferences.Editor systemSettingsEditor = systemSettings.edit();
        	systemSettingsEditor.putBoolean("FIRST_RUN", false);
        	systemSettingsEditor.putInt("TIME_SAVED", 0);
        	systemSettingsEditor.commit();
        	
        	// Set up some initial RSS Feed Values
        	FeedRepository repo = new FeedRepository(this);
        	List<Feed> feeds = repo.getFeeds();
        	// Read the file contents into a map for easier list management
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
    					feeds.add(new Feed(feedTitle, new URL(feedURL)));
    				}
    			}
    		} catch (Exception e){
    			Log.e("rollon", "XML Parsing Error",e);
    		}
    		
    		// Commit the feed changes
    		repo.setFeeds(feeds);
        }
        
        Intent i = new Intent(this, FeedsActivity.class);
        startActivity(i);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
