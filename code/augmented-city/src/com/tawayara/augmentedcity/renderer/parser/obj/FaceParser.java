package com.tawayara.augmentedcity.renderer.parser.obj;

import java.util.ArrayList;

import com.tawayara.augmentedcity.renderer.models.Group;
import com.tawayara.augmentedcity.renderer.parser.ParseException;

public class FaceParser {

	private static final int VERTEX_COUNT = 3;
	private static final int NORMALS_COUNT = 3;
	private static final int TEXTURE_COUNT = 2;
	private static final int NUMBER_OF_VERTICES_IN_FACE = 3;

	private SimpleTokenizer slashTokenizer;

	private class FacePart {
		public int vertexID = 0;
		public int textureID = -1;
		public int normalID = 0;
	}

	public FaceParser() {
		// Create a SimpleTokenizer that uses a slash as delimiter
		this.slashTokenizer = new SimpleTokenizer();
		this.slashTokenizer.setDelimiter("/");
	}

	public void parse(String model, ModelArrays modelArrays, Group currentGroup, int lineNumber, String line)
			throws ParseException {

		SimpleTokenizer spaceTokenizer = new SimpleTokenizer();
		spaceTokenizer.setStr(line.substring(2));

		verifyNumberOfVerticesForFace(model, lineNumber, spaceTokenizer);

		for (int i = 0; i < NUMBER_OF_VERTICES_IN_FACE; i++) {
			String facePartText = spaceTokenizer.next();
			processFacePart(model, modelArrays, currentGroup, lineNumber, facePartText);
		}
	}

	// If the number of vertices is not supported by the face specification a parse exception will
	// be raised. The number of used vertices must be 3 because only triangle faces are supported
	private void verifyNumberOfVerticesForFace(String modelName, int lineNumber,
			SimpleTokenizer spaceTokenizer) throws ParseException {
		int faces = spaceTokenizer.delimOccurCount() + 1;

		if (faces != NUMBER_OF_VERTICES_IN_FACE) {
			throw new ParseException(modelName, lineNumber, "only triangle faces are supported");
		}
	}

	private FacePart extractFacePart(String text, String modelName, int lineNumber)
			throws ParseException {
		this.slashTokenizer.setStr(text);

		int vertexCount = this.slashTokenizer.delimOccurCount() + 1;
		FacePart result = new FacePart();

		if (vertexCount == 2) {
			result.vertexID = this.slashTokenizer.nextAsInteger();
			result.textureID = this.slashTokenizer.nextAsInteger();
			throw new ParseException(modelName, lineNumber, "Vertex normal is necessary.");
		} else if (vertexCount == 3) {
			result.vertexID = this.slashTokenizer.nextAsInteger();
			result.textureID = this.slashTokenizer.nextAsInteger();
			result.normalID = this.slashTokenizer.nextAsInteger();
		} else {
			throw new ParseException(modelName, lineNumber,
					"a faces needs reference a vertex, a normal vertex and optionally a texture coordinate per vertex.");
		}

		return result;
	}

	private void verifyAndAdd(ArrayList<float[]> originalList, int id, ArrayList<Float> destList,
			int dimension) throws Exception {
		float[] vec = originalList.get(id);

		if (vec == null) {
			throw new Exception();
		}

		for (int j = 0; j < dimension; j++) {
			destList.add(vec[j]);
		}
	}

	private void processFacePart(String modelName, ModelArrays modelArrays, Group currentGroup,
			int lineNumber, String facePartText) throws ParseException {
		FacePart facePart = extractFacePart(facePartText, modelName, lineNumber);

		try {
			verifyAndAdd(modelArrays.vertices, facePart.vertexID, currentGroup.groupVertices, VERTEX_COUNT);
			verifyAndAdd(modelArrays.normals, facePart.normalID, currentGroup.groupNormals, NORMALS_COUNT);

			if (facePart.textureID != -1) {
				// in case there is a texture on the face
				verifyAndAdd(modelArrays.texcoords, facePart.textureID, currentGroup.groupTexcoords,
						TEXTURE_COUNT);
			}
		} catch (Exception e) {
			throw new ParseException(modelName, lineNumber, "Could not find reference.");
		}
	}
}
