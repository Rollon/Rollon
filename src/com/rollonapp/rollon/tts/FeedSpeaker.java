package com.rollonapp.rollon.tts;

import java.util.HashMap;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class FeedSpeaker extends TextToSpeech {
    private static final int CHUNK_SIZE = 2000;

    public FeedSpeaker(Context context, OnInitListener listener) {
        super(context, listener);
    }

    public int speakAll(String text) {

        final String speakingText = text;
        final FeedSpeaker speaker = this;

        this.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            private int location = 0;

            @Override
            public void onStart(String utteranceId) {
                Log.i("rollon", "Starting to speak text with ID: " + utteranceId);

            }

            @Override
            public void onError(String utteranceId) {
                Log.e("rollon", "There was an error speaking text with ID: " + utteranceId);

            }

            @Override
            public void onDone(String utteranceId) {
                int startLocation = location;

                location += CHUNK_SIZE;

                if (speakingText.length() < location) {
                    location = speakingText.length();
                } else if (location == speakingText.length()) {
                    Log.i("rollon", "Done speaking " + utteranceId);
                    return;
                }

                HashMap<String, String> opts = new HashMap<String, String>();
                opts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "currentText:" + startLocation + "-" + location);
                speaker.speak(speakingText.substring(startLocation, location), QUEUE_ADD, opts);
            }
        });
        
        HashMap<String, String> opts = new HashMap<String, String>();
        opts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "currentText:" + 0 + "-" + 0);
        this.speak("", QUEUE_ADD, opts);

        return SUCCESS;
    }
}
