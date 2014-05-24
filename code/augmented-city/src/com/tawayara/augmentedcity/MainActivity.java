package com.tawayara.augmentedcity;

import com.tawayara.gandar.GAndARActivity;

public class MainActivity extends GAndARActivity {

	private static final String SERVICE_URL = "http://gandar.tawayara.com";
	
	protected String getServiceUrl() {
		return SERVICE_URL;
	}

}
