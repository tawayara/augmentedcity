/**
	Copyright (C) 2009,2010  Tobias Domhan

    This file is part of AndOpenGLCam.

    AndObjViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AndObjViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AndObjViewer.  If not, see <http://www.gnu.org/licenses/>.
 
 */
package edu.dhbw.andar;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Debug;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.Window;
import android.view.WindowManager;
import edu.dhbw.andar.interfaces.OpenGLRenderer;

public abstract class AndARActivity extends Activity implements Callback, UncaughtExceptionHandler {

	private AndARView andarView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.currentThread().setUncaughtExceptionHandler(this);
		setFullscreen();
		disableScreenTurnOff();
		// orientation is set via the manifest

		andarView = new AndARView();
		setContentView(andarView.createView(this, this));

		if (Config.DEBUG)
			Debug.startMethodTracing("AndAR");
	}

	/**
	 * Set a renderer that draws non AR stuff. Optional, may be set to null or omited. and setups
	 * lighting stuff.
	 * 
	 * @param customRenderer
	 */
	public void setNonARRenderer(OpenGLRenderer customRenderer) {
		andarView.setNonARRenderer(customRenderer);
	}

	/**
	 * Avoid that the screen get's turned off by the system.
	 */
	public void disableScreenTurnOff() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * Set's the orientation to landscape, as this is needed by AndAR.
	 */
	public void setOrientation() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * Maximize the application.
	 */
	public void setFullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public void setNoTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onPause() {
		andarView.pause();
		super.onPause();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.runFinalization();
		if (Config.DEBUG)
			Debug.stopMethodTracing();
	}

	@Override
	protected void onResume() {
		andarView.resume();
		super.onResume();
	}

	protected void startPreview() {
		andarView.startPreview();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	/*
	 * The GLSurfaceView changed
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int,
	 * int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/*
	 * The GLSurfaceView was created The camera will be opened and the preview started
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		andarView.setSurfaceCreated(true);
	}

	/*
	 * GLSurfaceView was destroyed The camera will be closed and the preview stopped.
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
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
