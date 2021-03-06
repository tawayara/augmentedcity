package com.tawayara.gandar.renderer.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.tawayara.gandar.renderer.utils.BaseFileUtil;

public class Model implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//position/rotation/scale
	public float xrot = 90;
    public float yrot = 0;
    public float zrot = 0;
    public float xpos = 0;
    public float ypos = 0;
    public float zpos = 0;
    public float scale = 4f;
    public int STATE = STATE_DYNAMIC;
    public static final int STATE_DYNAMIC = 0;
    public static final int STATE_FINALIZED = 1;
    
	
	private Vector<Group> groups = new Vector<Group>();
	/**
	 * all materials
	 */
	protected HashMap<String, Material> materials = new HashMap<String, Material>();
	
	public Model() {
		//add default material
		materials.put("default",new Material("default"));
	}
	
	public void addMaterial(Material mat) {
		//mat.finalize();
		materials.put(mat.getName(), mat);
	}
	
	public Material getMaterial(String name) {
		return materials.get(name);
	}
	
	public void addGroup(Group grp) {
		if(STATE == STATE_FINALIZED)
			grp.finalize();
		groups.add(grp);
	}
	
	public Vector<Group> getGroups() {
		return groups;
	}
	
	public void setFileUtil(BaseFileUtil fileUtil) {
		for (Iterator<Material> iterator = materials.values().iterator(); iterator.hasNext();) {
			Material mat = iterator.next();
			mat.setFileUtil(fileUtil);
		}
	}
	
	
	public HashMap<String, Material> getMaterials() {
		return materials;
	}

	public void setScale(float f) {
		this.scale += f;
		if(this.scale < 0.0001f)
			this.scale = 0.0001f;
	}

	public void setXrot(float dY) {
		this.xrot += dY;
	}

	public void setYrot(float dX) {
		this.yrot += dX;
	}

	public void setXpos(float f) {
		this.xpos += f;
	}

	public void setYpos(float f) {
		this.ypos += f;
	}
	
	/**
	 * convert all dynamic arrays to final non alterable ones.
	 */
	public void finalize() {
		if(STATE != STATE_FINALIZED) {
			STATE = STATE_FINALIZED;
			for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
				Group grp = iterator.next();
				grp.finalize();
				grp.setMaterial(materials.get(grp.getMaterialName()));
			}
			for (Iterator<Material> iterator = materials.values().iterator(); iterator.hasNext();) {
				Material mtl = iterator.next();
				mtl.finalize();
			}
		}
	}
	
}
