package com.rollonapp.rollon;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CustomRssActivity extends Activity {

	private EditText inputFeed;
	private Button listenButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_rss);
		
		listenButton = (Button) findViewById(R.id.listenButton);
		
        final Activity activity = this;
		
        listenButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i  = new Intent(activity, RssFeedReaderActivity.class);
                i.setData(Uri.parse(inputFeed.getText().toString()));
                startActivity(i);
			}
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_rss, menu);
		return true;
	}

}
