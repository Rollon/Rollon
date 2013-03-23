package com.rollonapp.rollon.activities;

import net.htmlparser.jericho.Source;

/**
 * Subclass of ReaderActivity to handle reading RSS feeds.
 * 
 * @author paul
 *
 */
public class RssReaderActivity extends ReaderActivity {
    
    @Override
    public String onProcessText(String originalText) {
        Source source = new Source(originalText);
        source.fullSequentialParse();
        originalText = source.getTextExtractor().setIncludeAttributes(false).toString();
        
        return originalText;
    }
}
