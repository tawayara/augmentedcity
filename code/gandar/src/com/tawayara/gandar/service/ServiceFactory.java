package com.tawayara.gandar.service;

import com.tawayara.gandar.service.http.HttpServiceFactory;

public abstract class ServiceFactory {

	private static ServiceFactory instance;
	
	public static synchronized ServiceFactory getInstance(String baseUrl) {
		if (instance == null) {
			instance = new HttpServiceFactory(baseUrl);
		}
		
		return instance;
	}
	
	public abstract PointService getPointService();
}
