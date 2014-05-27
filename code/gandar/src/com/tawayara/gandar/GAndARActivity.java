package com.tawayara.gandar;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
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
import com.tawayara.gandar.renderer.utils.BaseFileUtil;
import com.tawayara.gandar.renderer.utils.CacheFileUtil;
import com.tawayara.gandar.renderer.utils.FromHttpToCache;
import com.tawayara.gandar.service.PointService;
import com.tawayara.gandar.service.ServiceFactory;
import com.tawayara.gandar.service.data.Point;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;
import edu.dhbw.andar.listener.AndARCameraListener;

/**
 * Class that aims to implement the AndARActivity in order to present a content over a marker based
 * on its geolocation. The marker will be the same but the content will change based on the latitude
 * and longitude provided by the user's device.
 */
public abstract class GAndARActivity extends AndARActivity implements UncaughtExceptionHandler {

	private static final String TAG = GAndARActivity.class.getSimpleName();
	private static final String BASE_FILES_PATH = "models/";
	private static final String MODEL_FILE_EXTENSION = ".obj";

	private Model model;
	private Model3D model3d;
	private ProgressDialog progressDialog;
	private ARToolkit artoolkit;

	/**
	 * Method that must be implemented by the class that will extend this one. This method must
	 * return the address of the server that will be used by the framework in order to retrieve the
	 * necessary augmented reality information (like content on a determined geolocation) and files
	 * (3D models to be presented over the marker).
	 * 
	 * @return The address of the server that will provide the necessary augmented reality
	 *         information and files.
	 */
	protected abstract String getServiceUrl();

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
	}

	@Override
	public void uncaughtException(Thread thread, Throwable error) {
		// For now, the error is just being presented on log
		Log.e(TAG, "Uncaught exception.", error);
	}

	// listener that will be called after the creation of the camera on AndARView
	private AndARCameraListener listener = new AndARCameraListener() {

		@Override
		public void onCameraCreated() {
			// The 3D model is being loaded here in order to assure that the surface that presents
			// camera content and the surface that receives 3D objects were already created
			if (GAndARActivity.this.model == null) {
				GAndARActivity.this.progressDialog = ProgressDialog.show(GAndARActivity.this,
						getResources().getText(R.string.loading_title),
						getResources().getText(R.string.loading), true);
				GAndARActivity.this.progressDialog.show();

				// Create and execute the task that is responsible to download the content (that
				// will be presented over the marker) from server
				new ModelLoader().execute();
			}
		}

	};

	// Retrieve the nearest point from server based on latitude and longitude
	private Point retrieveNearestPoint(int latitude, int longitude) throws ClientProtocolException,
			IOException {
		PointService service = ServiceFactory.getInstance(this.getServiceUrl()).getPointService();
		Point point = service.retrievePoint(latitude, longitude);
		this.downloadPointFilesOnCache(point);
		return point;
	}

	// Method that will download the files specified on the given point and store them on cache
	private void downloadPointFilesOnCache(Point point) throws ClientProtocolException, IOException {
		FromHttpToCache fromHttpToCache = new FromHttpToCache(GAndARActivity.this);
		fromHttpToCache.download(point.objUrl, BASE_FILES_PATH, point.name + ".obj");
		fromHttpToCache.download(point.mtlUrl, BASE_FILES_PATH, point.name + ".mtl");
		fromHttpToCache.download(point.textureUrl, BASE_FILES_PATH, point.name + ".png");
	}

	// Load the 3D model specified by the Point using the given parser
	private void loadModel(Point point, ObjParser parser) throws IOException, ParseException {
		this.model = parser.parse(point.name, point.name + MODEL_FILE_EXTENSION);
		this.model3d = new Model3D(this.model);
		this.model.setScale(0.0005f);
	}

	// method that aims to display an error message to the user if the content could not be
	// retrieved
	private void displayContentRetrievingError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.loading_error).setTitle(R.string.loading_error_title);
		AlertDialog dialog = builder.create();
		
		// Set the dismiss listener to close the current activity
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				GAndARActivity.this.finish();
			}
		});
		
		dialog.show();
	}

	// asynchronous task that will download the content from server based on current geolocation
	private class ModelLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// TODO retrieve latitude and longitude of the device
				int latitude = 0;
				int longitude = 0;

				// Retrieve point based on geolocation
				Point point = GAndARActivity.this.retrieveNearestPoint(latitude, longitude);

				// Create the BaseFileUtil instance that will be used by the parser. It is necessary
				// to specify that the files will be read from cache
				BaseFileUtil fileUtil = new CacheFileUtil(GAndARActivity.this);
				fileUtil.setBaseFolder(BASE_FILES_PATH);

				// Create the parser that will be used to load the 3D model
				ObjParser parser = new ObjParser(fileUtil);

				// Loading the 3D model
				GAndARActivity.this.loadModel(point, parser);
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
				// Release the ProgressDialog instance that is displaying the downloading progress
				GAndARActivity.this.progressDialog.dismiss();

				if (GAndARActivity.this.model3d != null) {
					// Register model on ARToolKit in order to present it over marker
					GAndARActivity.this.artoolkit.registerARObject(GAndARActivity.this.model3d);

					// Start the preview of the AndARView
					GAndARActivity.super.getAndARView().startPreview();
				} else {
					throw new Exception("3D model could not be loaded.");
				}
			} catch (AndARException e) {
				Log.e(TAG, "It was not possible to register the AR model.", e);
				GAndARActivity.this.displayContentRetrievingError();
			} catch (Throwable t) {
				Log.e(TAG, "Something got wrong during the registration of the content.", t);
				GAndARActivity.this.displayContentRetrievingError();
			}
		}
	}
}
