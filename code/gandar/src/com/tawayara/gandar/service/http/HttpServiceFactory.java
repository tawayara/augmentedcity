package com.tawayara.gandar.service.http;

import com.tawayara.gandar.service.PointService;
import com.tawayara.gandar.service.ServiceFactory;

public class HttpServiceFactory extends ServiceFactory {

	private String baseUrl;
	
	public HttpServiceFactory(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	@Override
	public PointService getPointService() {
		return new HttpPointService(this.baseUrl);
	}

}
