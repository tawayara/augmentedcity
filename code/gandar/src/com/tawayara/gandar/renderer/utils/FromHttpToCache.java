package com.tawayara.gandar.renderer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

/**
 * Class responsible to download a given URL and save it on a specified file on cache.
 */
public class FromHttpToCache {

	// The context to be used in order to retrieve cache directory
	private Context context;

	/**
	 * The default constructor of the class needs the context in order to retrieve the cache
	 * directory to be used.
	 * 
	 * @param context
	 *            The context to be used on cache information retrieving.
	 */
	public FromHttpToCache(Context context) {
		this.context = context;
	}

	/**
	 * Download and save the URL on the given path (using cache directory as base) with the
	 * specified file name.
	 * 
	 * @param url
	 *            The URL of the content to be retrieved.
	 * @param path
	 *            The path inside cache directory on where the file will be saved.
	 * @param fileName
	 *            The name of the file to be saved.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void download(String url, String path, String fileName) throws ClientProtocolException,
			IOException {
		File directory = this.createDirectoryIfNotExist(path);
		File file = this.ifFileExistsDeleteAndCreateANewOne(directory, fileName);
		HttpEntity entity = this.executeHttpRequest(url);

		if (entity != null) {
			this.saveHttpEntityOnFile(entity, file);
		}
	}

	// Creates the path inside cache directory
	private File createDirectoryIfNotExist(String path) {
		String basePath = this.context.getCacheDir().getPath();
		File directory = new File(basePath + File.separator + path);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}

	// Create a new file if another one was previously saved with the same name
	private File ifFileExistsDeleteAndCreateANewOne(File directory, String fileName)
			throws IOException {
		File file = new File(directory, fileName);
		if (file.exists()) {
			file.delete();
		}

		file.createNewFile();

		return file;
	}

	// Execute an HTTP request and return the HttpEntity object with the response
	private HttpEntity executeHttpRequest(String url) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(request);
		HttpEntity entity = httpResponse.getEntity();
		return entity;
	}

	// Save the content of an HttpEntity object on a given file
	private void saveHttpEntityOnFile(HttpEntity entity, File file) throws IllegalStateException,
			IOException {
		InputStream instream = entity.getContent();
		OutputStream output = new FileOutputStream(file);

		try {
			byte data[] = new byte[1024];
			int count;
			
			// read the data of the entity and save it on file
			while ((count = instream.read(data)) != -1) {
				output.write(data, 0, count);
			}
		} finally {
			// close the streams
			output.flush();
			output.close();
			instream.close();
		}
	}
}
