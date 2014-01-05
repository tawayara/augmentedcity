package edu.dhbw.andar;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.view.Window;
import android.view.WindowManager;
import edu.dhbw.andar.interfaces.OpenGLRenderer;
import edu.dhbw.andar.listener.AndARCameraListener;

/**
 * This class creates the base behavior for an Activity that uses the augmented reality
 * functionality from AndAR.
 * 
 * In order to work properly, it is necessary to set the orientation to landscape on the manifest
 * file for the Activity class that will implement it.
 */
public abstract class AndARActivity extends Activity implements UncaughtExceptionHandler {

	// Stores the AndARView instance that is being used by the Activity
	private AndARView andarView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.currentThread().setUncaughtExceptionHandler(this);

		// Change the UI to be presented in full screen mode and avoid that the screen get's turned
		// off by the system.
		this.setFullscreen();
		this.disableScreenTurnOff();

		// Create the view to be presented by the Activity
		andarView = new AndARView();
		setContentView(andarView.createView(this));

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
		andarView.pause();
		super.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		andarView.resume();
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
	 * Set a renderer that draws non AR stuff. Optional, may be set to null or omitted. and setups
	 * lighting stuff.
	 * 
	 * @param customRenderer
	 */
	protected void setNonARRenderer(OpenGLRenderer customRenderer) {
		andarView.setNonARRenderer(customRenderer);
	}

	protected void startPreview() {
		andarView.startPreview();
	}

	/**
	 * @return a the instance of the ARToolkit.
	 */
	protected ARToolkit getArtoolkit() {
		return andarView.getArtoolkit();
	}

	/**
	 * Takes a screenshot. This must not be called from the GUI thread, e.g. from methods like
	 * onCreateOptionsMenu and onOptionsItemSelected. It is necessary to use an AsyncTask for this
	 * purpose.
	 * 
	 * @return The Bitmap of the requested screenshot
	 */
	protected Bitmap takeScreenshot() {
		return andarView.takeScreenshot();
	}

	/**
	 * Add a listener to identify when the camera is fully loaded and ready to be used.
	 * 
	 * @param listener
	 *            The instance of the listener to be used.
	 */
	protected void setAndARCameraListener(AndARCameraListener listener) {
		this.andarView.setAndARCameraListener(listener);
	}

}
