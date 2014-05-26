package com.tawayara.gandar.renderer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CacheFileUtil extends BaseFileUtil {

	private Context context;

	public CacheFileUtil(Context context) {
		this.context = context;
	}

	@SuppressWarnings("resource")
	@Override
	public BufferedReader getReaderFromName(String name) {
		try {
			File dir = this.context.getCacheDir();
			//File dir = Environment.getExternalStorageDirectory();
			File file = new File(dir, File.separator + getBaseFolder() + name);
			InputStream is = new FileInputStream(file);
			return (is == null) ? null : new BufferedReader(new InputStreamReader(is));
			//return new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public Bitmap getBitmapFromName(String name) {
		try {
			File dir = this.context.getCacheDir();
			//File dir = Environment.getExternalStorageDirectory();
			File file = new File(dir, File.separator + getBaseFolder() + name);
			InputStream is = new FileInputStream(file);
			return (is == null) ? null : BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
