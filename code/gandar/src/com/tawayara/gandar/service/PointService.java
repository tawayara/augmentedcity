package com.tawayara.gandar.service;

import com.tawayara.gandar.service.data.Point;

/**
 * Interface that aims to define the communication with the service in order to retrieve information
 * about points.
 */
public interface PointService {

	/**
	 * Retrieve a point based on the given geolocation.
	 * 
	 * @param latitude
	 *            The value that should represent the current latitude of the device.
	 * @param longitude
	 *            The value that should represent the current longitude of the device.
	 * @return The nearest point based on geolocation or null if no point can be found.
	 */
	public Point retrievePoint(int latitude, int longitude);
}
