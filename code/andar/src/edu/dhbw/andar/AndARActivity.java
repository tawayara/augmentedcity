package edu.dhbw.andar;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Debug;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.Window;
import android.view.WindowManager;
import edu.dhbw.andar.interfaces.OpenGLRenderer;

/**
 * This class creates the base behavior for an Activity that uses the augmented reality
 * functionality from AndAR.
 * 
 * In order to work properly, it is necessary to set the orientation to landscape on the manifest
 * file for the Activity class that will implement it.
 */
public abstract class AndARActivity extends Activity implements Callback, UncaughtExceptionHandler {

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
		setContentView(andarView.createView(this, this));

		if (Config.DEBUG) {
			Debug.startMethodTracing("AndAR");
		}
	}

	private void setFullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

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
	public void setNonARRenderer(OpenGLRenderer customRenderer) {
		andarView.setNonARRenderer(customRenderer);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.runFinalization();
		
		if (Config.DEBUG)
			Debug.stopMethodTracing();
	}

	protected void startPreview() {
		andarView.startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		andarView.setSurfaceCreated(true);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	/**
	 * @return a the instance of the ARToolkit.
	 */
	public ARToolkit getArtoolkit() {
		return andarView.getArtoolkit();
	}

	/**
	 * Take a screenshot. Must not be called from the GUI thread, e.g. from methods like
	 * onCreateOptionsMenu and onOptionsItemSelected. You have to use a asynctask for this purpose.
	 * 
	 * @return the screenshot
	 */
	public Bitmap takeScreenshot() {
		return andarView.takeScreenshot();
	}

	public GLSurfaceView getSurfaceView() {
		return this.andarView.getSurfaceView();
	}

}
