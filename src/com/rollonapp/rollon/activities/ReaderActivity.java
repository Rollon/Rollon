package com.rollonapp.rollon.activities;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rollonapp.rollon.R;
import com.rollonapp.rollon.tts.FeedSpeaker;

/**
 * Generic reading activity, pass lists of title, subtitles, and texts to read
 * and display in the calling intent.
 * 
 * Subclass and override onProcessTitle(), onProcessSubtitle(), and
 * onProcessText() to change behavior or process passed data before reading.
 * 
 * In the calling Intent, pass it: com.rollonapp.rollon.ReaderTitles: A
 * {@code List<String>} of Titles, one for each text to be read.
 * com.rollonapp.rollon.ReaderSubtitles: A {@code List<String>} of Subtitles,
 * one for each text to be read. com.rollonapp.rollon.ReaderTexts: A
 * {@code List<String>} of Text to read.
 * 
 * @author Paul Tela
 * 
 */
public class ReaderActivity extends Activity implements TextToSpeech.OnInitListener {
    // Log tag
    protected static final String TAG = "rollon";

    // Data names for calling Intent
    protected static final String INTENT_EXTRA_TITLES = "com.rollonapp.rollon.ReaderTitles";
    protected static final String INTENT_EXTRA_SUBTITLES = "com.rollonapp.rollon.ReaderSubtitles";
    protected static final String INTENT_EXTRA_TEXTS = "com.rollonapp.rollon.ReaderTexts";

    // Views
    protected TextView title, subtitle, text;
    protected Button stopButton, skipButton;
    protected ProgressBar loadingIcon;

    // Text to speech
    protected FeedSpeaker tts;

    // Intent that triggered launch
    protected Intent callingIntent;

    // Keeping track of what will be read
    protected List<String> titles, subtitles, texts;
    protected int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        // Get the calling Intent and extract data from it
        callingIntent = getIntent();
        titles = callingIntent.getStringArrayListExtra(INTENT_EXTRA_TITLES);
        subtitles = callingIntent.getStringArrayListExtra(INTENT_EXTRA_SUBTITLES);
        texts = callingIntent.getStringArrayListExtra(INTENT_EXTRA_TEXTS);

        if (!inputDataValidates()) {
            Toast.makeText(this, "Invlaid reader information given!", Toast.LENGTH_LONG).show();
            finish();
        }

        position = 0;

        initViewVars();

        tts = new FeedSpeaker(this, this);
    }

    /**
     * Inflates the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rss_feed_reader, menu);
        return true;
    }

    /**
     * Called when the FeedSpeaker is ready
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "This Language is not supported");
            } else {
                processAndSpeak();
            }

        } else {
            Log.e(TAG, "TTS Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    /**
     * Called before Title is displayed. Called on background worker thread.
     * 
     * @param originalTitle
     *            The original title.
     * @return The new title to display.
     */
    protected String onProcessTitle(String originalTitle) {
        return originalTitle;
    }

    /**
     * Called before Subtitle is displayed or read. Called on background worker
     * thread.
     * 
     * @param originalSubtitle
     *            The original subtitle.
     * @return The new subtitle.
     */
    protected String onProcessSubtitle(String originalSubtitle) {
        return originalSubtitle;
    }

    /**
     * Called before Text is displayed or read. Called on background worker
     * thread.
     * 
     * @param originalText
     *            The original text.
     * @return
     */
    protected String onProcessText(String originalText) {
        return originalText;
    }

    /**
     * Initializes all needed View member variables.
     */
    protected void initViewVars() {
        title = (TextView) findViewById(R.id.readerTitle);
        subtitle = (TextView) findViewById(R.id.readerSubtitle);
        text = (TextView) findViewById(R.id.readerText);
        loadingIcon = (ProgressBar) findViewById(R.id.readerLoadingIcon);
    }

    /**
     * Validates the input data from the calling Intent.
     * 
     * @return Whether the data is valid.
     */
    protected boolean inputDataValidates() {
        return (titles != null && subtitles != null && texts != null && titles.size() == subtitles.size() && subtitles
                .size() == texts.size());
    }

    /**
     * Processes and speaks the first text.
     */
    protected void processAndSpeak() {
        processAndSpeak(position);
    }

    /**
     * Processes the title, subtitle, and text to be spoken and then speaks the
     * text.
     * 
     * @param pos
     *            The index of text to speak.
     */
    protected void processAndSpeak(int pos) {
        new ProcessAndSpeakTask(titles.get(pos), subtitles.get(pos), texts.get(pos)).execute();
    }

    /**
     * Handles calling the callback methods for processing each text on a
     * background thread and then speaks the final results.
     * 
     */
    private class ProcessAndSpeakTask extends AsyncTask<Void, Integer, Void> {
        private String title, subtitle, text;

        public ProcessAndSpeakTask(String title, String subtitle, String text) {
            super();
            this.title = title;
            this.subtitle = subtitle;
            this.text = text;
        }

        @Override
        protected Void doInBackground(Void... params) {
            title = ReaderActivity.this.onProcessTitle(title);
            subtitle = ReaderActivity.this.onProcessSubtitle(subtitle);
            text = ReaderActivity.this.onProcessText(text);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            ReaderActivity.this.text.setText(this.text);
            ReaderActivity.this.subtitle.setText(this.subtitle);
            ReaderActivity.this.title.setText(this.title);

            ReaderActivity.this.tts.speakAll(this.subtitle + " " + this.text);
        }

    }

}
