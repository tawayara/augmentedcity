package edu.dhbw.andar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.view.Window;
import android.view.WindowManager;

/**
 * This class creates the base behavior for an Activity that uses the augmented reality
 * functionality from AndAR.
 * 
 * In order to work properly, it is necessary to set the orientation to landscape on the manifest
 * file for the Activity class that will implement it.
 */
public abstract class AndARActivity extends Activity {

	// Stores the AndARView instance that is being used by the Activity
	private AndARView andarView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Change the UI to be presented in full screen mode and avoid that the screen get's turned
		// off by the system.
		this.setFullscreen();
		this.disableScreenTurnOff();

		// Create the view to be presented by the Activity
		this.andarView = new AndARView();
		setContentView(this.andarView.createView(this));

		// Start the method tracing
		if (Config.DEBUG) {
			Debug.startMethodTracing("AndAR");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.runFinalization();

		// Stop the method tracing
		if (Config.DEBUG)
			Debug.stopMethodTracing();
	}

	@Override
	protected void onPause() {
		this.andarView.pause();
		super.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		this.andarView.resume();
		super.onResume();
	}

	// Change the current activity to full screen mode
	private void setFullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	// Request the functionality to not allow the device to sleep
	private void disableScreenTurnOff() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * Retrieve the AndARView instance that is being used by the Activity.
	 * 
	 * @return The AndarView instance.
	 */
	protected AndARView getAndARView() {
		return this.andarView;
	}
}
