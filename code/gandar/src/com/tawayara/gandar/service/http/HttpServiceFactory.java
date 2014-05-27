package com.tawayara.gandar.service.http;

import com.tawayara.gandar.service.PointService;
import com.tawayara.gandar.service.ServiceFactory;

/**
 * Class that represents a factory of objects that will perform service calls through HTTP requests. 
 */
public class HttpServiceFactory extends ServiceFactory {

	// The base URL of the calls
	private String baseUrl;
	
	public HttpServiceFactory(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	@Override
	public PointService getPointService() {
		return new HttpPointService(this.baseUrl);
	}

}
