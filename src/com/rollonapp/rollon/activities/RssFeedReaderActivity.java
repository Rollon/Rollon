package com.rollonapp.rollon.activities;

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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rollonapp.rollon.R;
import com.rollonapp.rollon.tts.FeedSpeaker;

public class RssFeedReaderActivity extends Activity implements TextToSpeech.OnInitListener {

	private static final String ROLLONAPP_API_ROOT = "http://rollonapp.com/article/";

    private final String SYSTEM_SETTINGS = "SYSTEM";

	private int articlePos;
	
	private Time startTime;
	private Time endTime;

	private FeedSpeaker tts;
	private Intent callingIntent;

	private Button stopButton;
	private Button skipButton;
	private TextView feedName, articleTitle;
	private ProgressBar loadingIcon;

	private TextView articleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rss_feed_reader);
		stopButton = (Button) findViewById(R.id.stopButton);
		skipButton = (Button) findViewById(R.id.skipButton);
		feedName = (TextView) findViewById(R.id.feedName);
		articleTitle = (TextView) findViewById(R.id.articleTitle);
		loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);

		// Start the timer
		startTime = new Time();
		startTime.setToNow();

		// Initialize the speaker
		tts = new FeedSpeaker(this, this);
		final Activity activity = this;
		
		
		callingIntent = getIntent();
		articlePos = callingIntent.getIntExtra("ARTICLE_POSITION", 0);
		
		Log.d("rollon", "Got articlePos: " + articlePos);
		
		stopButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Stop the timer and increment the system value
				updateTimeSaved();
				finish();
			}
		});
		
		skipButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 // If it is reading, stop it.
                if ( tts != null ){
                    tts.stop();
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
				String callingIntentData = callingIntent.getDataString();
				getTextAndSpeak(callingIntentData, articlePos);
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	@Override
	public void onBackPressed(){
		// Stop the timer and increment the system value
		updateTimeSaved();
		super.onBackPressed();
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
	
	protected void getTextAndSpeak(String feedUrl, final int articlePosition) {
		AsyncTask<URL, Integer, ArticleInfo> task = new AsyncTask<URL, Integer, ArticleInfo>() {
		    private int articlePos  = articlePosition;
			@Override
			protected ArticleInfo doInBackground(URL... params) {
				String content = "", title = "";
				
				try {
					RssFeed feed = RssReader.read(params[0]);

					ArrayList<RssItem> items = feed.getRssItems();
					
					for ( RssItem x : items ){
						Log.i("debug","Article Title: "  + x.getTitle().trim());
					}
					
					if (articlePos >= items.size()) {
					    articlePos = 0;
					}
					
					RssItem item = null;
					try {
					    item = items.get(articlePos);
					} catch (IndexOutOfBoundsException e) {
					    Log.e("rollon", "Invalid article position given.", e);
					    return new ArticleInfo("Error", "There was an error reading this article.  Please try another.");
					}
					content = item.getContent();

					if (content == null || content.length() <= 0) {
						Log.i("rollon", "Getting content from link...");
						// Need to make our HTTP call
						content = getReadableArticleText(item.getLink());
					}
					
					title = item.getTitle();

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

				
				return new ArticleInfo(title, content);
			}

			protected void onPostExecute(ArticleInfo article) {
				String text = article.getText();
				String title = article.getTitle();

				articleTitle.setText(title);

				// Print the text to screen for debugging purposes.
				articleText = (TextView) findViewById(R.id.articleText);
				articleText.setText(text);

				// Filter each request if the feed name is "UNTRACKED_CUSTOM_RSS" add to file
				String finalFeedName = getIntent().getStringExtra("FEED_NAME");
				
				// Update the feed name title
				feedName.setText(finalFeedName);

				loadingIcon.setVisibility(View.GONE);

				tts.speakAll(title + " " + text);
			}

		};

		try {
			task.execute(new URL(feedUrl));
		} catch (MalformedURLException e) {
			Log.e("rollon", "Bad URL", e);
		}
	}

	private void updateTimeSaved(){
		// Stop the timer and increment the system value
		endTime = new Time();
		endTime.setToNow();

		// Access the settings on file
		SharedPreferences systemSettings = getSharedPreferences(SYSTEM_SETTINGS, 0);
		SharedPreferences.Editor systemSettingsEditor = systemSettings.edit();

		// Calculate the difference
		int tempTime = systemSettings.getInt("TIME_SAVED", -1);
		tempTime += ( ( endTime.toMillis(true) - startTime.toMillis(true) ) / 100000 );

		// Commit the changes for persistence
		systemSettingsEditor.putInt("TIME_SAVED", tempTime);
		systemSettingsEditor.apply();
	}
	
	protected String getReadableArticleText(String articleUrl) {
	    String text = "";
	    
        try {
            String argument = URLEncoder.encode(articleUrl, "UTF-8");
            URL apiUrl = new URL(ROLLONAPP_API_ROOT + argument);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            InputStream in  = new BufferedInputStream(conn.getInputStream());
            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
            text = s.next();
        } catch (IOException e) {
            Log.e("rollon", "Could not get article text.", e);
        }
	    
	    return text;
	}
	
	/**
	 * Stores information about the Article to be read, used to pass title and text around
	 * inside of AsyncTask, rather than using an ArrayList.
	 *
	 */
	private class ArticleInfo {
	    private String title, text;
	    
	    public ArticleInfo(String title, String text) {
	        this.title = title;
	        this.text = text;
	    }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
	    
	}

}
