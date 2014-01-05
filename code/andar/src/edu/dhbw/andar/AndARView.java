package edu.dhbw.andar;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.widget.FrameLayout;
import edu.dhbw.andar.camera.CameraManager;
import edu.dhbw.andar.exceptions.AndARRuntimeException;
import edu.dhbw.andar.interfaces.OpenGLRenderer;
import edu.dhbw.andar.util.IO;

public class AndARView {

	private GLSurfaceView glSurfaceView;
	private PreviewSurfaceView previewSurface;
	private AndARRenderer renderer;
	private ARToolkit artoolkit;
	private CameraManager cameraManager;

	public View createView(Context context, Callback callback) {
		artoolkit = new ARToolkit(context.getResources(), context.getFilesDir());

		try {
			IO.transferFilesToPrivateFS(context.getFilesDir(), context.getResources());
		} catch (IOException e) {
			throw new AndARRuntimeException(e.getMessage());
		}
		
		FrameLayout frame = new FrameLayout(context);
		cameraManager = new CameraManager();
		previewSurface = new PreviewSurfaceView(context);
		previewSurface.setCameraManager(cameraManager);

		glSurfaceView = new GLSurfaceView(context);
		renderer = new AndARRenderer(artoolkit);
		glSurfaceView.setRenderer(renderer);
		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		glSurfaceView.getHolder().addCallback(callback);

		cameraManager.setPreviewHandler(new CameraPreviewHandler(glSurfaceView, renderer, context
				.getResources(), artoolkit, new CameraStatus()));

		frame.addView(glSurfaceView);
		frame.addView(previewSurface);

		return frame;
	}

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
		this.previewSurface.startPreview();
	}

	public void setSurfaceCreated(boolean value) {
		previewSurface.setSurfaceCreated(value);
	}

	public GLSurfaceView getSurfaceView() {
		return this.glSurfaceView;
	}

	public void setNonARRenderer(OpenGLRenderer customRenderer) {
		this.renderer.setNonARRenderer(customRenderer);
	}

	public Bitmap takeScreenshot() {
		return renderer.takeScreenshot();
	}
}
