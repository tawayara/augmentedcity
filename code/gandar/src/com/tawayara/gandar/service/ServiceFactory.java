package com.tawayara.gandar.service;

public abstract class ServiceFactory {

	private static ServiceFactory instance;
	
	public static synchronized ServiceFactory getInstance() {
		if (instance == null) {
			instance = null;
		}
		
		return instance;
	}
	
	public abstract PointService getPointService();
}
