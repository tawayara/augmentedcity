package edu.dhbw.andar;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import edu.dhbw.andar.camera.CameraManager;
import edu.dhbw.andar.exceptions.AndARRuntimeException;
import edu.dhbw.andar.interfaces.OpenGLRenderer;
import edu.dhbw.andar.listener.AndARCameraListener;
import edu.dhbw.andar.util.IO;

public class AndARView {

	private GLSurfaceView glSurfaceView;
	private CameraSurfaceView cameraSurface;
	private AndARRenderer renderer;
	private ARToolkit artoolkit;
	private CameraManager cameraManager;

	public View createView(Context context) {
		this.initializeARToolKit(context);
		this.cameraManager = new CameraManager();
		this.createCameraSurface(context);
		this.createGLSurfaceView(context);
		this.cameraManager.setPreviewHandler(new CameraPreviewHandler(this.glSurfaceView, this.renderer, context
				.getResources(), this.artoolkit, new CameraStatus()));
		return this.createLayout(context);
	}
	
	private View createLayout(Context context) {
		FrameLayout frame = new FrameLayout(context);
		frame.addView(this.glSurfaceView);
		frame.addView(this.cameraSurface);
//		glSurfaceView.setZOrderMediaOverlay(true);
//		glSurfaceView.setZOrderOnTop(true);
		//frame.bringChildToFront(previewSurface);
		//frame.addView(glSurfaceView);

		return frame;
	}
	
	private void initializeARToolKit(Context context) {
		this.artoolkit = new ARToolkit(context.getResources(), context.getFilesDir());
		this.transferFilesToPrivateFS(context);
	}

	private void transferFilesToPrivateFS(Context context) {
		try {
			IO.transferFilesToPrivateFS(context.getFilesDir(), context.getResources());
		} catch (IOException e) {
			throw new AndARRuntimeException(e.getMessage());
		}
	}

	private void createCameraSurface(Context context) {
		cameraSurface = new CameraSurfaceView(context);
		cameraSurface.setCameraManager(cameraManager);
	}

	private void createGLSurfaceView(Context context) {
		renderer = new AndARRenderer(artoolkit);
		
		glSurfaceView = new GLSurfaceView(context);
		glSurfaceView.setRenderer(renderer);
		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		glSurfaceView.getHolder().addCallback(this.callback);
	}
	
	private Callback callback = new Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
		
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			cameraSurface.setSurfaceCreated(true);
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// TODO Auto-generated method stub
			
		}
	};

	public void pause() {
		this.glSurfaceView.onPause();
		this.cameraManager.pause();
	}

	public void resume() {
		this.glSurfaceView.onResume();
		this.cameraManager.resume();
	}

	public ARToolkit getArtoolkit() {
		return this.artoolkit;
	}

	public void startPreview() {
		this.cameraSurface.startPreview();
	}

	/**
	 * Set a renderer that draws non AR stuff. Optional, may be set to null or omitted. and setups
	 * lighting stuff.
	 * 
	 * @param customRenderer
	 */
	public void setNonARRenderer(OpenGLRenderer customRenderer) {
		this.renderer.setNonARRenderer(customRenderer);
	}

	/**
	 * Takes a screenshot. This must not be called from the GUI thread, e.g. from methods like
	 * onCreateOptionsMenu and onOptionsItemSelected. It is necessary to use an AsyncTask for this
	 * purpose.
	 * 
	 * @return The Bitmap of the requested screenshot
	 */
	public Bitmap takeScreenshot() {
		return renderer.takeScreenshot();
	}

	/**
	 * Add a listener to identify when the camera is fully loaded and ready to be used.
	 * 
	 * @param listener
	 *            The instance of the listener to be used.
	 */
	public void setAndARCameraListener(AndARCameraListener listener) {
		this.cameraSurface.setAndARCameraListener(listener);
	}
}
