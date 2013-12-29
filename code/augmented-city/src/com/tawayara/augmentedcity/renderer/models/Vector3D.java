package com.tawayara.augmentedcity.renderer.models;

import java.io.Serializable;

public class Vector3D implements Serializable {

	private static final long serialVersionUID = 1L;

	public float x = 0;
	public float y = 0;
	public float z = 0;

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector3D(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
