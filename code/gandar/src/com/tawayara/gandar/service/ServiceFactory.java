package com.tawayara.gandar.service;

import com.tawayara.gandar.service.http.HttpServiceFactory;

/**
 * Class that creates the objects that are responsible to retrieve content from server.
 */
public abstract class ServiceFactory {

	// stores the instance of the factory that will be used by the application
	private static ServiceFactory instance;

	/**
	 * Retrieve the instance of the factory. The instance will be created in the first call to this
	 * method. The subsequent calls will consider the base URL given in the first call.
	 * 
	 * @param baseUrl
	 *            The base URL to be used by the service calls.
	 * @return The proper factory instance.
	 */
	public static synchronized ServiceFactory getInstance(String baseUrl) {
		if (instance == null) {
			instance = new HttpServiceFactory(baseUrl);
		}

		return instance;
	}

	/**
	 * Creates the instance of the PointService interface to be used by the application.
	 * 
	 * @return The instance of the PointService interface.
	 */
	public abstract PointService getPointService();
}
