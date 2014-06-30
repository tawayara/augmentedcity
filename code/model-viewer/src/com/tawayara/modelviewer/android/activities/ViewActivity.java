package com.tawayara.modelviewer.android.activities;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.tawayara.gandar.renderer.LightingRenderer;
import com.tawayara.gandar.renderer.Model3D;
import com.tawayara.gandar.renderer.models.Model;
import com.tawayara.gandar.renderer.parser.ParseException;
import com.tawayara.gandar.renderer.parser.obj.ObjParser;
import com.tawayara.gandar.renderer.utils.AssetsFileUtil;
import com.tawayara.gandar.renderer.utils.BaseFileUtil;
import com.tawayara.modelviewer.R;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;
import edu.dhbw.andar.listener.AndARCameraListener;

public class ViewActivity extends AndARActivity implements UncaughtExceptionHandler {

	private static final String TAG = ViewActivity.class.getSimpleName();
	private static final String BASE_FILES_PATH = "models/";
	private static final String MODEL_FILE_EXTENSION = ".obj";

	public static final String EXTRA_MODEL_NAME = "model_name";
	
	private Model model;
	private Model3D model3d;
	private ProgressDialog progressDialog;
	private ARToolkit artoolkit;
	private String modelName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Change the current thread uncaught exceptions to be handled by this class
		Thread.currentThread().setUncaughtExceptionHandler(this);

		// Provide the renderer and camera listener to the AndARView
		super.getAndARView().setNonARRenderer(new LightingRenderer());
		super.getAndARView().setAndARCameraListener(this.listener);

		// Retrieve the ARToolKit instance of the AndARView to be used in this class
		this.artoolkit = super.getAndARView().getArtoolkit();
		
		this.modelName = getIntent().getStringExtra(EXTRA_MODEL_NAME);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable error) {
		// For now, the error is just being presented on log
		Log.e(TAG, "Uncaught exception.", error);
	}
	
	private void loadModel() {
		// The 3D model is being loaded here in order to assure that the surface that presents
		// camera content and the surface that receives 3D objects were already created
		if (this.model == null) {
			try {
				this.progressDialog = ProgressDialog.show(ViewActivity.this, getResources()
						.getText(R.string.app_name),
						getResources().getText(R.string.app_name), true);
				this.progressDialog.setCancelable(true);
				this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						ViewActivity.this.finish();
					}
				});
				this.progressDialog.show();

				// Create and execute the task that is responsible to download the content (that will be
				// presented over the marker) from server
				new ModelLoader().execute();
			} catch (Exception e) {
				Log.e(TAG, "Error while creating the dialog.", e);
			}
		}
	}

	// listener that will be called after the creation of the camera on AndARView
	private AndARCameraListener listener = new AndARCameraListener() {

		@Override
		public void onCameraCreated() {
			ViewActivity.this.loadModel();
		}

	};

	// Load the 3D model specified by the name using the given parser
	private void loadModel(String name, ObjParser parser) throws IOException, ParseException {
		this.model = parser.parse(name, name + MODEL_FILE_EXTENSION);
		this.model3d = new Model3D(this.model);
		this.model.setScale(0.0005f);
	}

	// asynchronous task that will download the content from server based on current geolocation
	private class ModelLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// Create the BaseFileUtil instance that will be used by the parser. It is necessary
				// to specify that the files will be read from cache
				BaseFileUtil fileUtil = new AssetsFileUtil(ViewActivity.this.getResources().getAssets());
				fileUtil.setBaseFolder(BASE_FILES_PATH);

				// Create the parser that will be used to load the 3D model
				ObjParser parser = new ObjParser(fileUtil);

				// Loading the 3D model
				ViewActivity.this.loadModel(ViewActivity.this.modelName, parser);
			} catch (IOException e) {
				Log.e(TAG, "It was not possible to read the 3D model file", e);
			} catch (ParseException e) {
				Log.e(TAG, "A problem happened reading the 3D model.", e);
			} catch (Throwable t) {
				Log.e(TAG, "A problem happened on 3D model loading.", t);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			try {
				try {
					// Release the ProgressDialog instance that is displaying the downloading
					// progress
					ViewActivity.this.progressDialog.dismiss();
				} catch (Exception e) {
					Log.e(TAG, "Error while releasing the dialog.", e);
				}

				if (ViewActivity.this.model3d != null) {
					// Register model on ARToolKit in order to present it over marker
					ViewActivity.this.artoolkit.registerARObject(ViewActivity.this.model3d);

					// Start the preview of the AndARView
					ViewActivity.super.getAndARView().startPreview();
				} else {
					throw new Exception("3D model could not be loaded.");
				}
			} catch (AndARException e) {
				Log.e(TAG, "It was not possible to register the AR model.", e);
			} catch (Throwable t) {
				Log.e(TAG, "Something got wrong during the registration of the content.", t);
			}
		}
	}
}
