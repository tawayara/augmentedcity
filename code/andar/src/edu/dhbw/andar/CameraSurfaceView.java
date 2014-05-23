package edu.dhbw.andar;

import java.io.IOException;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.dhbw.andar.camera.CameraManager;
import edu.dhbw.andar.listener.AndARCameraListener;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private CameraManager cameraManager;
	private SurfaceHolder surfaceHolder;
	private boolean surfaceCreated = false;
	private int width;
	private int height;
	private AndARCameraListener listener;
    
    public CameraSurfaceView(Context context) {
        super(context);
        
        // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
    	cameraManager.stopPreview();
        surfaceHolder = null;
    }
    
    private void start() {
    	if (this.surfaceCreated) {
    		try {
				this.cameraManager.startPreview(this.surfaceHolder, this.width, this.height);
				
				if (this.listener != null) {
					this.listener.onCameraCreated();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	this.surfaceHolder = holder;
    	this.width = height;
    	this.height = width;
//    	this.width = width;
//    	this.height = height;
    	// TODO identify width and height based on screen rotation
    	this.start();
    }
    
    public void setSurfaceCreated(boolean surfaceCreated) {
    	this.surfaceCreated = surfaceCreated;
    }

	public void startPreview() {
    	this.start();
	}

	/**
	 * Add a listener to identify when the camera is fully loaded and ready to be used.
	 * 
	 * @param listener
	 *            The instance of the listener to be used.
	 */
	public void setAndARCameraListener(AndARCameraListener listener) {
		this.listener = listener;
	}
}
