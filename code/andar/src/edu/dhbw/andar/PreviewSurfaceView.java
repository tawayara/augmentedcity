package edu.dhbw.andar;

import java.io.IOException;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.dhbw.andar.camera.CameraManager;

public class PreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private CameraManager cameraManager;
	private SurfaceHolder surfaceHolder;
	private boolean surfaceCreated = false;
	private int width;
	private int height;
    
    public PreviewSurfaceView(Context context, CameraManager cameraManager) {
        super(context);
        
        this.cameraManager = cameraManager;
        
        // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	this.surfaceHolder = holder;
    	this.width = width;
    	this.height = height;
    	this.start();
    }
    
    public void setSurfaceCreated(boolean surfaceCreated) {
    	this.surfaceCreated = surfaceCreated;
    }

	public void startPreview() {
    	this.start();
	}
}
