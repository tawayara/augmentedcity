package com.tawayara.gandar.service.http;

import com.tawayara.gandar.service.PointService;
import com.tawayara.gandar.service.data.Point;

class HttpPointService implements PointService {

	private String baseUrl;

	public HttpPointService(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public Point retrievePoint(int latitude, int longitude) {
		// TODO Auto-generated method stub

		//String url = baseUrl + "/point.php?latitude=" + latitude + "&longitude=" + longitude;

		Point result = new Point();
		
//		result.name = "Teemo";
//		result.description = "the Swift Scout";
//		result.objUrl = baseUrl + "/models/Teemo.obj";
//		result.mtlUrl = baseUrl + "/models/Teemo.mtl";
//		result.textureUrl = baseUrl + "/models/Teemo.png";

//		result.name = "Rammus";
//		result.description = "the Armordillo";
//		result.objUrl = baseUrl + "/models/Rammus.obj";
//		result.mtlUrl = baseUrl + "/models/Rammus.mtl";
//		result.textureUrl = baseUrl + "/models/Rammus.png";

//		result.name = "Ziggs";
//		result.description = "the Hexplosives Expert";
//		result.objUrl = baseUrl + "/models/Ziggs.obj";
//		result.mtlUrl = baseUrl + "/models/Ziggs.mtl";
//		result.textureUrl = baseUrl + "/models/Ziggs.png";

//		result.name = "Tristana";
//		result.description = "the Megling Gunner";
//		result.objUrl = baseUrl + "/models/Tristana.obj";
//		result.mtlUrl = baseUrl + "/models/Tristana.mtl";
//		result.textureUrl = baseUrl + "/models/Tristana.png";

//		result.name = "LuLu";
//		result.description = "?";
//		result.objUrl = baseUrl + "/models/LuLu.obj";
//		result.mtlUrl = baseUrl + "/models/LuLu.mtl";
//		result.textureUrl = baseUrl + "/models/LuLu.png";

//		result.name = "Kennen";
//		result.description = "?";
//		result.objUrl = baseUrl + "/models/Kennen.obj";
//		result.mtlUrl = baseUrl + "/models/Kennen.mtl";
//		result.textureUrl = baseUrl + "/models/Kennen.png";

		result.name = "Annie";
		result.description = "?";
		result.objUrl = baseUrl + "/models/Annie.obj";
		result.mtlUrl = baseUrl + "/models/Annie.mtl";
		result.textureUrl = baseUrl + "/models/Annie.png";
		
		result.latitude = 0;
		result.longitude = 0;
		return result;
	}

}
