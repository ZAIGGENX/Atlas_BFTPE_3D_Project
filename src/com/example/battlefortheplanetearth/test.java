package com.example.battlefortheplanetearth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.TextView;

/*private*/public class test extends Activity {
	
	TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		//mTextView = (TextView) findViewById(R.id.txt);
		
		AsyncTask task = new AsyncTask<Void, Integer, Void>() {
		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 10; i-- >= 0; i-- ) {
				try {
					Thread.sleep(1000);
					//mTextView.setText(i);
				} catch (Exception e) {	}
			}
			return null;
			}
		};

		task.execute();
	}
}
