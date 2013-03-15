package com.rollonapp.rollon.activities;

import com.rollonapp.rollon.R;
import com.rollonapp.rollon.R.id;
import com.rollonapp.rollon.R.layout;
import com.rollonapp.rollon.R.menu;

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

		// Collect form elements
		listenButton = (Button) findViewById(R.id.listenButton);
		inputFeed = (EditText) findViewById(R.id.inputFeed);

		// Save the activity to start a new intent when listenButton is pressed
		final Activity activity = this;

		// Launch the selection into the current reading.
		listenButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(activity, RssFeedReaderActivity.class);
				i.setData(Uri.parse(inputFeed.getText().toString()));
				i.putExtra("FEED_NAME", "UNTRACKED_CUSTOM_RSS");
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
