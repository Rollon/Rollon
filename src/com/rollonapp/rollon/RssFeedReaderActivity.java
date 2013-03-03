package com.rollonapp.rollon;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.htmlparser.jericho.Source;
import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RssFeedReaderActivity extends Activity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    private Button stopButton;
    private TextView feedName, articleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feed_reader);
        stopButton = (Button) findViewById(R.id.stopButton);
        feedName = (TextView) findViewById(R.id.feedName);
        articleTitle = (TextView) findViewById(R.id.articleTitle);
        
        Intent callingIntent = getIntent();
        String callingIntentData = callingIntent.getDataString();
        String feedNameFromIntent = callingIntent.getStringExtra("FEED_NAME");
        
        feedName.setText(feedNameFromIntent);

        tts = new TextToSpeech(this, this);
        
        stopButton.setOnClickListener(new View.OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		finish();
        	}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rss_feed_reader, menu);
        return true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                Intent callingIntent = getIntent();
                String callingIntentData = callingIntent.getDataString();
            	getTextAndSpeak(callingIntentData);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    
    protected void getTextAndSpeak(String feedUrl) {
    	final Activity activity = this;
        AsyncTask<URL, Integer, List<String>> task = new AsyncTask<URL, Integer, List<String>>() {

            @Override
            protected ArrayList<String> doInBackground(URL... params) {
                String content, title = "";
                try {
                    RssFeed feed = RssReader.read(params[0]);
                    
                    ArrayList<RssItem> items = feed.getRssItems();

                    RssItem first = items.get(0);
                    content = first.getContent();
                    Log.i("rollon", "content: " + content);
                    
                    if (content == null || content.length() <= 0) {
                        Log.i("rollon", "Getting content from link...");
                        // Need to make our HTTP call
                        String argument = URLEncoder.encode(first.getLink(), "UTF-8");
                        URL apiUrl = new URL("http://rollonapp.com/article/" + argument);
                        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
                        try {
                            InputStream in  = new BufferedInputStream(conn.getInputStream());
                            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
                            content = s.next();
                        } catch (IOException e) {
                            Log.e("rollon", "Could not get article text.", e);
                            content = "";
                        }
                    }
                    title = first.getTitle();
                    content = first.getTitle() + "... " + content;
                    
                } catch (SAXException e) {
                    Log.e("rollon", "Error playing", e);
                    content = "";
                } catch (IOException e) {
                    Log.e("rollon", "Error playing", e);
                    content = "";
                } catch (Exception e) {
                	Log.e("rollon", "Error: ", e);
                	content = "";
                }
                
                // Put it through the filter
                Source source = new Source(content);
                source.fullSequentialParse();
                content = source.getTextExtractor().setIncludeAttributes(false).toString();
                
                Log.i("rollon", "Final content: " + content);
                
                ArrayList<String> list = new ArrayList<String>(2);
                list.add(title);
                list.add(content);
                return list;
            }
            
            protected void onPostExecute(List<String> list) {
                String text = list.get(1);
                String title = list.get(0);
                
                articleTitle.setText(title);
                
                int length = (text.length() < 3000) ? text.length() : 3000;
                tts.speak(text.substring(0,  length), TextToSpeech.QUEUE_FLUSH, null);
            }
            
        };
        
        try {
            task.execute(new URL(feedUrl));
        } catch (MalformedURLException e) {
            Log.e("rollon", "Bad URL", e);
        }
    }

}
