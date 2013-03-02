package com.rollonapp.rollon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import android.widget.EditText;
import android.widget.TextView;

public class RssFeedReaderActivity extends Activity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    private EditText rssFeedUrl;
    private Button readRssUrlButton, speakRssButton;
    private TextView rssResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feed_reader);

        rssFeedUrl = (EditText) findViewById(R.id.rssFeedUrl);
        readRssUrlButton = (Button) findViewById(R.id.readRssUrlButton);
        speakRssButton = (Button) findViewById(R.id.speakRssButton);
        rssResult = (TextView) findViewById(R.id.rssResult);

        Intent callingIntent = getIntent();
        String callingIntentData = callingIntent.getDataString();
        
        rssFeedUrl.setText(callingIntentData);


        tts = new TextToSpeech(this, this);
        
        readRssUrlButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AsyncTask<URL, Integer, String> task = new AsyncTask<URL, Integer, String>() {

                    @Override
                    protected String doInBackground(URL... params) {
                        try {
                            RssFeed feed = RssReader.read(params[0]);
                            ArrayList<RssItem> rssItems = feed.getRssItems();

                            StringBuilder rss = new StringBuilder();
                            for (RssItem rssItem : rssItems) {

                                Source source = new Source(rssItem.getContent());
                                source.fullSequentialParse();

                                rss.append(rssItem.getTitle());
                                rss.append(source.getTextExtractor().setIncludeAttributes(false).toString());
                            }
                            return rss.toString();
                        } catch (SAXException e) {
                            Log.e("rollon", "Could not parse RSS feed", e);
                            return "";
                        } catch (IOException e) {
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
        
        speakRssButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String text = rssResult.getText().toString();

                int res = tts.speak(text.substring(0,  2000), TextToSpeech.QUEUE_FLUSH, null);
                if (res == TextToSpeech.ERROR) {
                    Log.e("rollon", "There was an error saying things");
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakRssButton.setEnabled(true);
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

}
