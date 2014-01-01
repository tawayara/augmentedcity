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
		// global vertices/normals
		ArrayList<float[]> vertices = new ArrayList<float[]>(1000);
		ArrayList<float[]> normals = new ArrayList<float[]>(1000);
		ArrayList<float[]> texcoords = new ArrayList<float[]>();

		Model model = new Model();
		Group curGroup = new Group();
		SimpleTokenizer slashTokenizer = new SimpleTokenizer();
		slashTokenizer.setDelimiter("/");

		String line;
		int lineNum = 1;
		for (line = is.readLine(); line != null; line = is.readLine(), lineNum++) {
			if (line.length() > 0) {
				if (line.startsWith("#")) {
					// just ignore lines with comments
				} else if (line.startsWith("v ")) {
					vertices.add(parseVertices(line));
				} else if (line.startsWith("vt ")) {
					texcoords.add(parseTextureCoordinate(line));
				} else if (line.startsWith("vn ")) {
					normals.add(parseNormal(line));
				} else if (line.startsWith("f ")) {
					// add face to group
					SimpleTokenizer spaceTokenizer = new SimpleTokenizer();
					String endOfLine = line.substring(2);
					spaceTokenizer.setStr(endOfLine);
					int faces = spaceTokenizer.delimOccurCount() + 1;
					if (faces != 3) {
						throw new ParseException(modelName, lineNum,
								"only triangle faces are supported");
					}
					for (int i = 0; i < 3; i++) {// only triangles supported
						String face = spaceTokenizer.next();
						slashTokenizer.setStr(face);
						int vertexCount = slashTokenizer.delimOccurCount() + 1;
						int vertexID = 0;
						int textureID = -1;
						int normalID = 0;
						if (vertexCount == 2) {
							// vertex reference
							vertexID = Integer.parseInt(slashTokenizer.next()) - 1;
							// normal reference
							normalID = Integer.parseInt(slashTokenizer.next()) - 1;
							throw new ParseException(modelName, lineNum, "vertex normal needed.");
						} else if (vertexCount == 3) {
							// vertex reference
							vertexID = Integer.parseInt(slashTokenizer.next()) - 1;
							String texCoord = slashTokenizer.next();
							if (!texCoord.equals("")) {
								// might be omitted
								// texture coord reference
								textureID = Integer.parseInt(texCoord) - 1;
							}
							// normal reference
							normalID = Integer.parseInt(slashTokenizer.next()) - 1;
						} else {
							throw new ParseException(modelName, lineNum,
									"a faces needs reference a vertex, a normal vertex and optionally a texture coordinate per vertex.");
						}
						float[] vec;
						try {
							vec = vertices.get(vertexID);
						} catch (IndexOutOfBoundsException ex) {
							throw new ParseException(modelName, lineNum,
									"non existing vertex referenced.");
						}
						if (vec == null)
							throw new ParseException(modelName, lineNum,
									"non existing vertex referenced.");
						for (int j = 0; j < VERTEX_DIMENSIONS; j++)
							curGroup.groupVertices.add(vec[j]);
						if (textureID != -1) {
							// in case there is a texture on the face
							try {
								vec = texcoords.get(textureID);
							} catch (IndexOutOfBoundsException ex) {
								throw new ParseException(modelName, lineNum,
										"non existing texture coord referenced.");
							}
							if (vec == null)
								throw new ParseException(modelName, lineNum,
										"non existing texture coordinate referenced.");
							for (int j = 0; j < TEXTURE_COORD_DIMENSIONS; j++)
								curGroup.groupTexcoords.add(vec[j]);
						}
						try {
							vec = normals.get(normalID);
						} catch (IndexOutOfBoundsException ex) {
							throw new ParseException(modelName, lineNum,
									"non existing normal vertex referenced.");
						}
						if (vec == null)
							throw new ParseException(modelName, lineNum,
									"non existing normal vertex referenced.");
						for (int j = 0; j < VERTEX_DIMENSIONS; j++)
							curGroup.groupNormals.add(vec[j]);
					}
				} else if (line.startsWith("mtllib ")) {
					// parse material file
					// get ID of the mtl file
					String filename = line.substring(7);
					String[] files = Util.splitBySpace(filename);
					for (int i = 0; i < files.length; i++) {
						BufferedReader mtlFile = fileUtil.getReaderFromName(files[i]);
						if (mtlFile != null) {
							MtlParser mtlParser = new MtlParser(fileUtil);
							mtlParser.parse(model, mtlFile);
						}
					}
				} else if (line.startsWith("usemtl ")) {
					// material changed -> new group
					if (curGroup.groupVertices.size() > 0) {
						model.addGroup(curGroup);
						curGroup = new Group();
					}
					// the rest of the line contains the name of the new material
					curGroup.setMaterialName(line.substring(7));
				} else if (line.startsWith("g ")) {
					// new group definition
					if (curGroup.groupVertices.size() > 0) {
						model.addGroup(curGroup);
						curGroup = new Group();
						// group name will be ignored so far...is there any use?
					}
				}
			}
		}
		if (curGroup.groupVertices.size() > 0) {
			model.addGroup(curGroup);
		}
		Iterator<Group> groupIt = model.getGroups().iterator();
		while (groupIt.hasNext()) {
			Group group = (Group) groupIt.next();
			group.setMaterial(model.getMaterial(group.getMaterialName()));
		}
		return model;
	}
}