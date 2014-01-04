package com.tawayara.augmentedcity.renderer.parser.obj;

import java.util.ArrayList;

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
