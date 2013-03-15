package com.rollonapp.rollon.tts;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

public class FeedUtteranceProgressListener extends UtteranceProgressListener {
    
    private static final int CHUNK_SIZE = 2000;
    
    private final TextToSpeech tts;
    private String textToSpeak;
    private int location;
    
    public FeedUtteranceProgressListener(TextToSpeech tts, String textToSpeak) {
        this.tts = tts;
        this.textToSpeak = textToSpeak;
        this.location = 0;
        
    }

    @Override
    public void onDone(String utteranceId) {
       
    }

    @Override
    public void onError(String utteranceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStart(String utteranceId) {
        // TODO Auto-generated method stub

    }

}
