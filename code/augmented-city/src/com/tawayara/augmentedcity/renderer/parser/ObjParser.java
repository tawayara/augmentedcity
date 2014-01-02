package com.tawayara.augmentedcity.renderer.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.tawayara.augmentedcity.renderer.models.Group;
import com.tawayara.augmentedcity.renderer.models.Model;
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
	private final int VERTEX_DIMENSIONS = 3;
	private final int TEXTURE_COORD_DIMENSIONS = 2;
	private final int NUMBER_OF_VERTICES_IN_FACE = 3;

	private BaseFileUtil fileUtil;

	public ObjParser(BaseFileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	private SimpleTokenizer createTokenizer(String text) {
		SimpleTokenizer spaceTokenizer = new SimpleTokenizer();
		spaceTokenizer.setStr(text);
		return spaceTokenizer;
	}

	private float[] parseVertices(String line) {
		SimpleTokenizer spaceTokenizer = createTokenizer(line.substring(2));
		float[] vertices = new float[] { Float.parseFloat(spaceTokenizer.next()),
				Float.parseFloat(spaceTokenizer.next()), Float.parseFloat(spaceTokenizer.next()) };
		return vertices;
	}

	private float[] parseTextureCoordinate(String line) {
		SimpleTokenizer spaceTokenizer = createTokenizer(line.substring(3));
		float[] coordinates = new float[] { Float.parseFloat(spaceTokenizer.next()),
				Float.parseFloat(spaceTokenizer.next()) };
		return coordinates;
	}

	private float[] parseNormal(String line) {
		SimpleTokenizer spaceTokenizer = createTokenizer(line.substring(3));
		float[] normal = new float[] { Float.parseFloat(spaceTokenizer.next()),
				Float.parseFloat(spaceTokenizer.next()), Float.parseFloat(spaceTokenizer.next()) };
		return normal;
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

	private void verifyNumberOfVerticesForFace(String modelName, int lineNumber,
			SimpleTokenizer spaceTokenizer) throws ParseException {
		int faces = spaceTokenizer.delimOccurCount() + 1;
		
		if (faces != NUMBER_OF_VERTICES_IN_FACE) {
			throw new ParseException(modelName, lineNumber, "only triangle faces are supported");
		}
	}

	private void parseFace(String modelName, ArrayList<float[]> vertices,
			ArrayList<float[]> normals, ArrayList<float[]> texcoords, Group currentGroup,
			int lineNumber, String line) throws ParseException {
		// Create a SimpleTokenizer that uses a slash as delimiter
		SimpleTokenizer slashTokenizer = new SimpleTokenizer();
		slashTokenizer.setDelimiter("/");

		SimpleTokenizer spaceTokenizer = createTokenizer(line.substring(2));

		// If the number of vertices is not supported by the face specification a parse exception
		// will be raised. The number of used vertices must be 3 because only triangle faces are
		// supported
		verifyNumberOfVerticesForFace(modelName, lineNumber, spaceTokenizer);

		for (int i = 0; i < NUMBER_OF_VERTICES_IN_FACE; i++) {
			// apply the proper value to the slash tokenizer
			slashTokenizer.setStr(spaceTokenizer.next());
			
			int vertexCount = slashTokenizer.delimOccurCount() + 1;
			int vertexID = 0;
			int textureID = -1;
			int normalID = 0;
			if (vertexCount == 2) {
				vertexID = slashTokenizer.nextAsInteger();
				normalID = slashTokenizer.nextAsInteger();
				throw new ParseException(modelName, lineNumber, "vertex normal needed.");
			} else if (vertexCount == 3) {
				vertexID = slashTokenizer.nextAsInteger();
				textureID = slashTokenizer.nextAsInteger();
				normalID = slashTokenizer.nextAsInteger();
			} else {
				throw new ParseException(modelName, lineNumber,
						"a faces needs reference a vertex, a normal vertex and optionally a texture coordinate per vertex.");
			}

			float[] vec;
			try {
				vec = vertices.get(vertexID);
			} catch (IndexOutOfBoundsException ex) {
				throw new ParseException(modelName, lineNumber, "non existing vertex referenced.");
			}
			if (vec == null)
				throw new ParseException(modelName, lineNumber, "non existing vertex referenced.");
			for (int j = 0; j < VERTEX_DIMENSIONS; j++)
				currentGroup.groupVertices.add(vec[j]);
			if (textureID != -1) {
				// in case there is a texture on the face
				try {
					vec = texcoords.get(textureID);
				} catch (IndexOutOfBoundsException ex) {
					throw new ParseException(modelName, lineNumber,
							"non existing texture coord referenced.");
				}
				if (vec == null)
					throw new ParseException(modelName, lineNumber,
							"non existing texture coordinate referenced.");
				for (int j = 0; j < TEXTURE_COORD_DIMENSIONS; j++)
					currentGroup.groupTexcoords.add(vec[j]);
			}
			try {
				vec = normals.get(normalID);
			} catch (IndexOutOfBoundsException ex) {
				throw new ParseException(modelName, lineNumber,
						"non existing normal vertex referenced.");
			}
			if (vec == null)
				throw new ParseException(modelName, lineNumber,
						"non existing normal vertex referenced.");
			for (int j = 0; j < VERTEX_DIMENSIONS; j++)
				currentGroup.groupNormals.add(vec[j]);
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
		
		ArrayList<float[]> vertices = new ArrayList<float[]>(1000);
		ArrayList<float[]> normals = new ArrayList<float[]>(1000);
		ArrayList<float[]> texcoords = new ArrayList<float[]>();

		int lineNumber = 1;
		for (String line = is.readLine(); line != null; line = is.readLine(), lineNumber++) {
			if (line.length() > 0) {
				if (line.startsWith("#")) {
					// just ignore lines with comments
				} else if (line.startsWith("v ")) {
					vertices.add(parseVertices(line));
				} else if (line.startsWith("vt ")) {
					texcoords.add(parseTextureCoordinate(line));
				} else if (line.startsWith("vn ")) {
					normals.add(parseNormal(line));
				} else if (line.startsWith("g ")) {
					currentGroup = verifyAndCreateNewGroup(model, currentGroup);
				} else if (line.startsWith("usemtl ")) {
					currentGroup = verifyAndCreateNewGroup(model, currentGroup);
					currentGroup.setMaterialName(line.substring(7));
				} else if (line.startsWith("mtllib ")) {
					parseMaterials(model, line);
				} else if (line.startsWith("f ")) {
					parseFace(modelName, vertices, normals, texcoords, currentGroup, lineNumber,
							line);
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