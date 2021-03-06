package com.tawayara.gandar;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
public abstract class GAndARActivity extends AndARActivity implements UncaughtExceptionHandler,
		LocationListener {

	private static final String TAG = GAndARActivity.class.getSimpleName();
	private static final String BASE_FILES_PATH = "models/";
	private static final String MODEL_FILE_EXTENSION = ".obj";
	private static final int GPS_CONFIG_REQUEST = 1986;

	private Model model;
	private Model3D model3d;
	private ProgressDialog progressDialog;
	private ARToolkit artoolkit;
	private LocationManager locationManager;
	private int latitude;
	private int longitude;
	private boolean shouldLoadFromGps;

	private boolean cameraIsReady;
	private boolean gpsConfigIsReady;

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

		this.shouldLoadFromGps = false;
		this.cameraIsReady = false;
		this.gpsConfigIsReady = false;

		// Change the current thread uncaught exceptions to be handled by this class
		Thread.currentThread().setUncaughtExceptionHandler(this);

		// Provide the renderer and camera listener to the AndARView
		super.getAndARView().setNonARRenderer(new LightingRenderer());
		super.getAndARView().setAndARCameraListener(this.listener);

		// Retrieve the ARToolKit instance of the AndARView to be used in this class
		this.artoolkit = super.getAndARView().getArtoolkit();

		// In order to use GPS
		this.locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.verifyGpsSettings();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Removing the Activity from GPS changing notification
		this.locationManager.removeUpdates(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable error) {
		// For now, the error is just being presented on log
		Log.e(TAG, "Uncaught exception.", error);
	}

	@Override
	public void onLocationChanged(Location location) {
		// Removing the Activity from GPS changing notification
		this.locationManager.removeUpdates(this);
		this.loadLocationData(location);
	}
	
	private synchronized void loadLocationData(Location location) {
		try {
			if (this.progressDialog != null && this.progressDialog.isShowing()) {
				this.progressDialog.setMessage(getResources().getText(R.string.retrieving_content));
			} else {
				this.progressDialog = ProgressDialog.show(GAndARActivity.this, getResources()
						.getText(R.string.loading_title),
						getResources().getText(R.string.retrieving_content_new_location), true);
				this.progressDialog.setCancelable(true);
				this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						GAndARActivity.this.finish();
					}
				});
				
				this.progressDialog.show();
			}
		} catch (Exception e) {
			Log.e(TAG, "Error while updating the dialog message.", e);
		}

		this.latitude = (int) (location.getLatitude() * 1E6);
		this.longitude = (int) (location.getLongitude() * 1E6);

		Log.d(TAG, "Loading model on: " + this.latitude + " - " + this.longitude);

		// Create and execute the task that is responsible to download the content (that will be
		// presented over the marker) from server
		new ModelLoader().execute();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Method called when GPS is disabled
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Method called when GPS is enabled
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Method called when GPS status changes: OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE e
		// AVAILABLE
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// TODO in order to work, it is necessary to remove the finish() from AndARActivity.resume()
		if (requestCode == GPS_CONFIG_REQUEST) {
			this.verifyGpsSettings();
		}
	}

	// Check if GPS is enabled. If it is not enabled, ask the user if it is the better time to go to
	// the GSP settings.
	private void verifyGpsSettings() {
		if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.gps_message).setTitle(R.string.gps_title);
			// Add the buttons
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, GPS_CONFIG_REQUEST);
				}
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					GAndARActivity.this.gpsConfigIsReady = true;
					GAndARActivity.this.ping();
				}
			});

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			this.shouldLoadFromGps = true;
			this.gpsConfigIsReady = true;
			this.ping();
		}
	}

	private synchronized void ping() {
		if (this.cameraIsReady && this.gpsConfigIsReady) {
			// TODO use this location
//			Location lastKnownLocation;
//			if (shouldLoadFromGps) {
//				lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			} else {
//				lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			}
			
			//this.loadLocationData(lastKnownLocation);
			this.startLocationRetrieving();
		}
	}

	private void startLocationRetrieving() {
		// The 3D model is being loaded here in order to assure that the surface that presents
		// camera content and the surface that receives 3D objects were already created
		if (this.model == null) {
			try {
				this.progressDialog = ProgressDialog.show(GAndARActivity.this, getResources()
						.getText(R.string.loading_title),
						getResources().getText(R.string.retrieving_location), true);
				this.progressDialog.setCancelable(true);
				this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						GAndARActivity.this.finish();
					}
				});
				this.progressDialog.show();
			} catch (Exception e) {
				Log.e(TAG, "Error while creating the dialog.", e);
			}

			// Registering the Activity to receive GPS changing notification
			if (this.shouldLoadFromGps) {
				this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
						GAndARActivity.this);
			}

			this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					GAndARActivity.this);
		}
	}

	// listener that will be called after the creation of the camera on AndARView
	private AndARCameraListener listener = new AndARCameraListener() {

		@Override
		public void onCameraCreated() {
			GAndARActivity.this.cameraIsReady = true;
			GAndARActivity.this.ping();
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
				try {
					// Release the ProgressDialog instance that is displaying the downloading
					// progress
					GAndARActivity.this.progressDialog.dismiss();
				} catch (Exception e) {
					Log.e(TAG, "Error while releasing the dialog.", e);
				}

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
