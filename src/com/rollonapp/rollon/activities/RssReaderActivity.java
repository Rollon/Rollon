package com.rollonapp.rollon.activities;

import java.util.List;

import com.rollonapp.rollon.api.RollonApi;

import net.htmlparser.jericho.Source;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Subclass of ReaderActivity to handle reading RSS feeds.
 * 
 * Adds an extra Intent field, for whether the "text" needs to be looked up with
 * the Rollon API or is ready to read.  Should be an {@code ArrayList<String>} 
 * where each element is either an empty string if the text does not need to be
 * looked up or a URL of where to lookup the text. 
 * 
 * @author Paul Tela
 * 
 */
public class RssReaderActivity extends ReaderActivity {

    public static final String INTENT_EXTRA_LOOKUP_ARTICLE_URL = "com.rollonapp.rollon.LookupArticleUrl";
    
    protected List<String> urls;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load urls
        urls = callingIntent.getStringArrayListExtra(INTENT_EXTRA_LOOKUP_ARTICLE_URL);
        
        if (urls == null || urls.size() != titles.size()) {
            Toast.makeText(this, "Error processing Rss Feed in Rss Reader", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Bad LOOKUP_ARTICLE_URL passed to RssReader.");
        }
    }

    @Override
    public String onProcessText(String originalText) {
        
        String url = urls.get(position);
        
        if (url.length() > 0) {
            originalText = RollonApi.getReadableArticleText(url);
        }
        
        Source source = new Source(originalText);
        source.fullSequentialParse();
        originalText = source.getTextExtractor().setIncludeAttributes(false).toString();

        return originalText;
    }
}
