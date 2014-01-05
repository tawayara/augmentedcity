package com.tawayara.augmentedcity.renderer.parser.obj;

import java.util.ArrayList;

/**
 * Class that aims to hold all arrays to be used in the 3D model creation.
 */
public class ModelArrays {
	public ArrayList<float[]> vertices;
	public ArrayList<float[]> normals;
	public ArrayList<float[]> texcoords;
	
	public ModelArrays() {
		this.vertices = new ArrayList<float[]>(1000);
		this.normals = new ArrayList<float[]>(1000);
		this.texcoords = new ArrayList<float[]>();
	}
}
