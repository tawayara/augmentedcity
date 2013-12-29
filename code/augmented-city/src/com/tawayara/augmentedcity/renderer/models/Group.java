package com.tawayara.augmentedcity.renderer.models;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import com.tawayara.augmentedcity.renderer.utils.MemUtil;

/**
 * a group of faces.
 * @author Tobi
 *
 */
public class Group implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String materialName = "default";
	private transient Material material;
	/**
	 * is there a texture associated with this group?
	 */
	private boolean textured = false;
	//access not via getters for performance reasons
	public transient FloatBuffer vertices = null;
	public transient FloatBuffer texcoords = null;
	public transient FloatBuffer normals = null;
	public int vertexCount = 0;
	
	public ArrayList<Float> groupVertices = new ArrayList<Float>(500);
	public ArrayList<Float> groupNormals = new ArrayList<Float>(500);
	public ArrayList<Float> groupTexcoords = new ArrayList<Float>();
	
	public Group() {
	}
	
	public void setMaterialName(String currMat) {
		this.materialName = currMat;
	}
	
	public String getMaterialName() {
		return materialName;
	}
	
	
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		if(texcoords != null && material != null && material.hasTexture()) {
			textured = true;
		}
		if(material != null)
			this.material = material;
	}

	public boolean containsVertices() {
		if(groupVertices != null)
			return groupVertices.size()>0;
		else if(vertices != null)
			return vertices.capacity()>0;
		else 
			return false;
	}
	
	public void setTextured(boolean b) {
		textured = b;
	}
	
	public boolean isTextured() {
		return textured;
	}
	
	/**
	 *  convert all dynamic arrays to final non alterable ones.
	 */
	public void finalize() {
		if (groupTexcoords.size() > 0) {
			textured = true;
			texcoords = MemUtil.makeFloatBuffer(groupTexcoords.size());
			for (Iterator<Float> iterator = groupTexcoords.iterator(); iterator.hasNext();) {
				Float curVal = iterator.next();
				texcoords.put(curVal.floatValue());				
			}
			texcoords.position(0);
			if(material != null && material.hasTexture()) {
				textured = true;
			} else {
				textured = false;
			}
		}
		groupTexcoords = null;
		//System.gc();
		vertices = MemUtil.makeFloatBuffer(groupVertices.size());
		vertexCount = groupVertices.size()/3;//three floats pers vertex
		for (Iterator<Float> iterator = groupVertices.iterator(); iterator.hasNext();) {
			Float curVal = iterator.next();
			vertices.put(curVal.floatValue());
		}
		//let the garbage collector free the memory
		groupVertices = null;
		//System.gc();
		normals = MemUtil.makeFloatBuffer(groupNormals.size());
		for (Iterator<Float> iterator = groupNormals.iterator(); iterator.hasNext();) {
			Float curVal =  iterator.next();
			normals.put(curVal.floatValue());
		}
		//let the garbage collector free the memory
		groupNormals = null;
		//System.gc();
		vertices.position(0);
		normals.position(0);		
	}
	
	/*
	 * get  a google protocol buffers builder, that may be serialized
	 */
	/*public BufferGroup getProtocolBuffer() {
		ModelProtocolBuffer.BufferGroup.Builder builder = ModelProtocolBuffer.BufferGroup.newBuilder();
		
		return builder.build();
	}*/
	
}
