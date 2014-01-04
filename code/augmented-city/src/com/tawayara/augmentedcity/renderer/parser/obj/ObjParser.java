package com.tawayara.augmentedcity.renderer.parser.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

import com.tawayara.augmentedcity.renderer.models.Group;
import com.tawayara.augmentedcity.renderer.models.Model;
import com.tawayara.augmentedcity.renderer.parser.ParseException;
import com.tawayara.augmentedcity.renderer.utils.BaseFileUtil;

/**
 * This class aims to parse an Wavefront OBJ file on a 3D model recognized by the renderer.
 * 
 * It does not support the full OBJ specification yet. The actual support includes: vertices,
 * normals, texture coordinates, basic materials (using .mat file), faces (faces may not omit the
 * face normal), limited texture support (through the map_Kd statement but only image files, no
 * options are recognized).
 * 
 */
public class ObjParser {

	private BaseFileUtil fileUtil;

	public ObjParser(BaseFileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	private void parseMaterials(Model model, String line) {
		String filename = line.substring(7);
		String[] files = Util.splitBySpace(filename);

		for (int i = 0; i < files.length; i++) {
			BufferedReader mtlFile = fileUtil.getReaderFromName(files[i]);
			if (mtlFile != null) {
				MtlParser mtlParser = new MtlParser(fileUtil);
				mtlParser.parse(model, mtlFile);
			}
		}
	}

	private Group verifyAndCreateNewGroup(Model model, Group group) {
		// verify if the group already contains values inside it in order to create a new one only
		// if necessary
		if (group.groupVertices.size() > 0) {
			// add the current group to the model before create a new one
			model.addGroup(group);

			// create the new group instance to be returned
			group = new Group();
		}

		return group;
	}

	private void updateGroupsMaterials(Model model) {
		Iterator<Group> groupIt = model.getGroups().iterator();
		while (groupIt.hasNext()) {
			Group group = (Group) groupIt.next();
			group.setMaterial(model.getMaterial(group.getMaterialName()));
		}
	}

	/**
	 * Parses an Wavefront OBJ file on a model recognized by the renderer.
	 * 
	 * @param modelName
	 *            name of the model
	 * @param is
	 *            stream of the file to parse
	 * @return the parsed model
	 * @throws IOException
	 * @throws ParseException
	 */
	public Model parse(String modelName, BufferedReader is) throws IOException, ParseException {
		Model model = new Model();
		Group currentGroup = new Group();
		ModelArrays modelArrays = new ModelArrays();

		LineToFloatArrayParser verticesParser = new LineToFloatArrayParser("v ", 3);
		LineToFloatArrayParser normalsParser = new LineToFloatArrayParser("vn ", 3);
		LineToFloatArrayParser textCoordParser = new LineToFloatArrayParser("vt ", 2);
		FaceParser faceParser = new FaceParser();

		int lineNumber = 1;
		for (String line = is.readLine(); line != null; line = is.readLine(), lineNumber++) {
			if (line.length() > 0) {
				if (line.startsWith("#")) {
					// just ignore lines with comments
				} else if (verticesParser.canRead(line)) {
					modelArrays.vertices.add(verticesParser.parse(line));
				} else if (normalsParser.canRead(line)) {
					modelArrays.normals.add(normalsParser.parse(line));
				} else if (textCoordParser.canRead(line)) {
					modelArrays.texcoords.add(textCoordParser.parse(line));
				} else if (line.startsWith("g ")) {
					currentGroup = verifyAndCreateNewGroup(model, currentGroup);
				} else if (line.startsWith("usemtl ")) {
					currentGroup = verifyAndCreateNewGroup(model, currentGroup);
					currentGroup.setMaterialName(line.substring(7));
				} else if (line.startsWith("mtllib ")) {
					parseMaterials(model, line);
				} else if (line.startsWith("f ")) {
					faceParser.parse(modelName, modelArrays, currentGroup, lineNumber, line);
				}
			}
		}

		// add the last used group to the model
		if (currentGroup.groupVertices.size() > 0) {
			model.addGroup(currentGroup);
		}

		// apply groups materials based on the given material name
		updateGroupsMaterials(model);

		return model;
	}
}
