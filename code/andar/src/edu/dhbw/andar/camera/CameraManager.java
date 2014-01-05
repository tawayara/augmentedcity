package edu.dhbw.andar.camera;

import java.io.IOException;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import edu.dhbw.andar.CameraHolder;
import edu.dhbw.andar.CameraParameters;
import edu.dhbw.andar.CameraPreviewHandler;
import edu.dhbw.andar.Config;

public class CameraManager {

	private Camera camera;
	private CameraPreviewHandler cameraPreviewHandler;
	private boolean paused = false;

	public void pause() {
		if (this.cameraPreviewHandler != null)
			this.cameraPreviewHandler.stopThreads();
	}

	public void resume() {
		this.paused = false;
	}

	private void openCamera(SurfaceHolder surfaceHolder, int width, int height) throws IOException {
		if (this.camera == null) {
			this.camera = CameraHolder.instance().open();
			this.camera.setPreviewDisplay(surfaceHolder);
			CameraParameters.setCameraParameters(this.camera, width, height);

			if (!Config.USE_ONE_SHOT_PREVIEW) {
				this.camera.setPreviewCallback(this.cameraPreviewHandler);
			}

			this.cameraPreviewHandler.init(this.camera);
		}
	}

	private void closeCamera() {
		if (camera != null) {
			CameraHolder.instance().keep();
			CameraHolder.instance().release();
			this.camera = null;
			this.cameraPreviewHandler.getCameraStatus().previewing = false;
		}
	}

	/**
	 * Open the camera and start detecting markers. note: You must assure that the preview surface
	 * already exists!
	 * 
	 * @throws IOException
	 */
	public void startPreview(SurfaceHolder surfaceHolder, int width, int height) throws IOException {
		if (this.paused)
			return;

		if (this.cameraPreviewHandler.getCameraStatus().previewing)
			this.stopPreview();

		this.openCamera(surfaceHolder, width, height);
		this.camera.startPreview();
		this.cameraPreviewHandler.getCameraStatus().previewing = true;
	}

	/**
	 * Close the camera and stop detecting markers.
	 */
	public void stopPreview() {
		if (this.camera != null && this.cameraPreviewHandler.getCameraStatus().previewing) {
			this.cameraPreviewHandler.getCameraStatus().previewing = false;
			this.camera.stopPreview();
		}

		this.closeCamera();
	}

	public void setPreviewHandler(CameraPreviewHandler cameraPreviewHandler) {
		this.cameraPreviewHandler = cameraPreviewHandler;
	}
}
