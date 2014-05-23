package com.tawayara.augmentedcity;

import java.io.BufferedReader;
import java.io.IOException;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.tawayara.augmentedcity.renderer.LightingRenderer;
import com.tawayara.augmentedcity.renderer.Model3D;
import com.tawayara.augmentedcity.renderer.models.Model;
import com.tawayara.augmentedcity.renderer.parser.ParseException;
import com.tawayara.augmentedcity.renderer.parser.obj.ObjParser;
import com.tawayara.augmentedcity.renderer.utils.AssetsFileUtil;
import com.tawayara.augmentedcity.renderer.utils.BaseFileUtil;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARFragment;
import edu.dhbw.andar.exceptions.AndARException;
import edu.dhbw.andar.listener.AndARCameraListener;

public class ViewActivity extends FragmentActivity {

	private static final String TAG = ViewActivity.class.getSimpleName();
	
	private Model model;
	private Model3D model3d;
	private ProgressDialog waitDialog;
	private ARToolkit artoolkit;
	private AndARFragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		
		FragmentManager manager = getFragmentManager();
		this.fragment = (AndARFragment) manager.findFragmentById(R.id.andar_fragment);

		this.fragment.getAndARView().setNonARRenderer(new LightingRenderer());
		this.fragment.getAndARView().setAndARCameraListener(this.listener);
		
		this.artoolkit = this.fragment.getAndARView().getArtoolkit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view, menu);
		return true;
	}
	
	private AndARCameraListener listener = new AndARCameraListener() {

		@Override
		public void onCameraCreated() {
			// The 3D model is being loaded here in order to assure that the surface was already created
			if (model == null) {
				waitDialog = ProgressDialog.show(ViewActivity.this, "", getResources().getText(R.string.loading),
						true);
				waitDialog.show();
				new ModelLoader().execute();
			}
		}
		
	};

	private class ModelLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			String modelFileName = "Teemo.obj";
			BaseFileUtil fileUtil = new AssetsFileUtil(getResources().getAssets());
			fileUtil.setBaseFolder("models/");

			// read the model file:
			if (modelFileName.endsWith(".obj")) {
				ObjParser parser = new ObjParser(fileUtil);
				try {
					if (fileUtil != null) {
						BufferedReader fileReader = fileUtil.getReaderFromName(modelFileName);
						if (fileReader != null) {
							model = parser.parse("Model", fileReader);
							model3d = new Model3D(model);
							model.setScale(0.0005f);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			waitDialog.dismiss();

			// register model
			try {
				if (model3d != null) {
					artoolkit.registerARObject(model3d);
				}
			} catch (AndARException e) {
				Log.e(TAG, "It was not possible to register the AR model.", e);
			}
			
			Log.w("OPA", "start preview");
			fragment.getAndARView().startPreview();
		}
	}

}
