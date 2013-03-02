package com.rollonapp.rollon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import net.htmlparser.jericho.Source;
import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RssFeedReaderActivity extends Activity {
    
    
    private EditText rssFeedUrl;
    private Button readRssUrlButton;
    private TextView rssResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feed_reader);
        
        rssFeedUrl = (EditText) findViewById(R.id.rssFeedUrl);
        readRssUrlButton = (Button) findViewById(R.id.readRssUrlButton);
        rssResult = (TextView) findViewById(R.id.rssResult);
        
        rssFeedUrl.setText("http://feeds.feedburner.com/TechCrunch/");
        
        readRssUrlButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
              
                AsyncTask<URL, Integer, String> task =  new AsyncTask<URL, Integer, String>() {

                    @Override
                    protected String doInBackground(URL... params) {
                        try {
                            RssFeed feed = RssReader.read(params[0]);
                            ArrayList<RssItem> rssItems = feed.getRssItems();
                            
                            StringBuilder rss = new StringBuilder();
                            for(RssItem rssItem : rssItems) {
                                Log.i("RSS Reader title", rssItem.getTitle());
                                Log.i("RSS Reader content", rssItem.getContent());
                                
                                Source source = new Source(rssItem.getContent());
                                source.fullSequentialParse();
                                
                                rss.append(rssItem.getTitle());
                                rss.append(source.getTextExtractor().setIncludeAttributes(false).toString());
                            }
                            return rss.toString();
                        } catch (SAXException e) {
                            // TODO Auto-generated catch block
                            Log.e("rollon", "Could not parse RSS feed", e);
                            return "";
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Log.e("rollon", "IOExcpetion Could not parse RSS feed", e);
                            return "";
                        }
                    }
                    
                    @Override
                    protected void onProgressUpdate(Integer... progress) {
                        
                    }
                    
                    @Override
                    protected void onPostExecute(String result) {
                        rssResult.setText(result);
                    }
                };
                
                try {
                    URL url = new URL(rssFeedUrl.getText().toString());
                    task.execute(url);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                   Log.e("rollon", "Bad URL entered.");
                   rssResult.setText("Invalid URL entered, try again.");
                }
                
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rss_feed_reader, menu);
        return true;
    }

}
