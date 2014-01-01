package com.tawayara.augmentedcity;

import java.io.BufferedReader;
import java.io.IOException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.view.Menu;
import android.view.SurfaceHolder;

import com.tawayara.augmentedcity.renderer.LightingRenderer;
import com.tawayara.augmentedcity.renderer.Model3D;
import com.tawayara.augmentedcity.renderer.models.Model;
import com.tawayara.augmentedcity.renderer.parser.ObjParser;
import com.tawayara.augmentedcity.renderer.parser.ParseException;
import com.tawayara.augmentedcity.renderer.utils.AssetsFileUtil;
import com.tawayara.augmentedcity.renderer.utils.BaseFileUtil;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.Config;
import edu.dhbw.andar.exceptions.AndARException;

public class MainActivity extends AndARActivity implements
		SurfaceHolder.Callback {

	private Model model;
	private Model3D model3d;
	private ProgressDialog waitDialog;
	ARToolkit artoolkit;

	public MainActivity() {
		super(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setNonARRenderer(new LightingRenderer());// or might be omited
		artoolkit = getArtoolkit();
		// getSurfaceView().setOnTouchListener(new TouchEventHandler());
		getSurfaceView().getHolder().addCallback(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		// load the model
		// this is done here, to assure the surface was already created, so that
		// the preview can be started
		// after loading the model
		if (model == null) {
			waitDialog = ProgressDialog.show(this, "",
					getResources().getText(R.string.loading), true);
			waitDialog.show();
			new ModelLoader().execute();
		}
	}

	private class ModelLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			//String modelFileName = "superman.obj";
			String modelFileName = "Teemo.obj";
			BaseFileUtil fileUtil = null;
			fileUtil = new AssetsFileUtil(getResources().getAssets());
			fileUtil.setBaseFolder("models/");

			// read the model file:
			if (modelFileName.endsWith(".obj")) {
				ObjParser parser = new ObjParser(fileUtil);
				try {
					if (fileUtil != null) {
						BufferedReader fileReader = fileUtil
								.getReaderFromName(modelFileName);
						if (fileReader != null) {
							model = parser.parse("Model", fileReader);
							model3d = new Model3D(model);
							//model.setScale(25.0f);
							model.setScale(0.05f);
						}
					}
					if (Config.DEBUG)
						Debug.stopMethodTracing();
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
				if (model3d != null)
					artoolkit.registerARObject(model3d);
			} catch (AndARException e) {
				e.printStackTrace();
			}
			startPreview();
		}
	}

}
