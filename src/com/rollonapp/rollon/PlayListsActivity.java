package com.rollonapp.rollon;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlayListsActivity extends Activity implements TextToSpeech.OnInitListener {
    
    private TextToSpeech tts;
    private Button speakButton;
    private EditText speakText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_lists);
        
        tts = new TextToSpeech(this, this);
        
        speakButton = (Button) findViewById(R.id.speakButton);
        speakText = (EditText) findViewById(R.id.speakText);
        
        speakButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String text = speakText.getText().toString();
                
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play_lists, menu);
        return true;
    }

    @Override
    public void onInit(int status) {
       if (status == TextToSpeech.SUCCESS) {
           int result = tts.setLanguage(Locale.UK);
           
           if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
               Log.e("rollon", "Language not supported, giving up.");
           } else {
               // Enable all the things
               speakButton.setEnabled(true);
               Log.i("rollon", "Ready for action.");
           }
       } else {
           Log.e("rollon", "Could not init TextToSpeech");
       }
        
    }

}
