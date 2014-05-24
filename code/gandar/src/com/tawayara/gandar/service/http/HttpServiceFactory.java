package com.tawayara.gandar.service.http;

import com.tawayara.gandar.service.PointService;
import com.tawayara.gandar.service.ServiceFactory;

public class HttpServiceFactory extends ServiceFactory {

	@Override
	public PointService getPointService() {
		return new HttpPointService();
	}

}
