package com.tawayara.gandar.service;

import com.tawayara.gandar.service.data.Point;

public interface PointService {
	public Point retrievePoint(int latitude, int longitude);
}
