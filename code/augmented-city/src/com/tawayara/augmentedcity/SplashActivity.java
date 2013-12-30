package com.tawayara.augmentedcity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new StartupTask().execute();
	}

	private class StartupTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);

			// call the activity finish method in order to avoid access the screen again when press
			// the back button
			finish();

			return null;
		}

	}
}
