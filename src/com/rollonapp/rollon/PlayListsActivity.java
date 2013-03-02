package com.rollonapp.rollon;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PlayListsActivity extends Activity implements TextToSpeech.OnInitListener {
    
    private ListView playlistView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_lists);
        
        playlistView = (ListView) findViewById(R.id.playlists);
        String[] values = new String[] { "Driving to work", "Driving home", "Lazy Sunday", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        playlistView.setAdapter(adapter);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play_lists, menu);
        return true;
    }

    @Override
    public void onInit(int status) {
        
    }

}
