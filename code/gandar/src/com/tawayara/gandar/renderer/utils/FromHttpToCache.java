package com.tawayara.gandar.renderer.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

public class FromHttpToCache {

	private Context context;

	public FromHttpToCache(Context context) {
		this.context = context;
	}

	public void download(String url, String path, String cacheFileName) {
		try {
			String basePath = this.context.getCacheDir().getPath();
			//String basePath = Environment.getExternalStorageDirectory().getPath();
			File dir = new File(basePath + File.separator + path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			File file = new File(dir, cacheFileName);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();
			
            HttpClient httpClient = new DefaultHttpClient();
    		HttpGet request = new HttpGet(url);
    		HttpResponse httpResponse = httpClient.execute(request);
    		
    		HttpEntity entity = httpResponse.getEntity();
    		
    		if (entity != null) {
				InputStream instream = entity.getContent();
				OutputStream output = new FileOutputStream(file);
	            byte data[] = new byte[1024];
	            int count;
	            while ((count = instream.read(data)) != -1) {
	                output.write(data, 0, count);
	            }
	            
	            output.flush();
	            output.close();
	            instream.close();
    		}
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static byte[] decodeStream(InputStream inputStream) {
		ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream(); 

		byte[] b = new byte[1024];
         
        try {
			while ( inputStream.read(b) != -1)
				bufferOutputStream.write(b);
		} catch (IOException e) {
			// TODO identify a better way to handle it
			try {
				bufferOutputStream.close();
				if(inputStream != null){
					inputStream.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
        return bufferOutputStream.toByteArray();
	}
}
