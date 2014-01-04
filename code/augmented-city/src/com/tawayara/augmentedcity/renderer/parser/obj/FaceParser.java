package com.tawayara.augmentedcity.renderer.parser.obj;

import java.util.ArrayList;

import com.tawayara.augmentedcity.renderer.models.Group;
import com.tawayara.augmentedcity.renderer.parser.ParseException;

public class FaceParser {

	private static final int NUMBER_OF_VERTICES_IN_FACE = 3;

	private Tokenizer slashTokenizer;

	private class FacePart {
		public int vertex = 0;
		public int texture = -1;
		public int normal = 0;
	}

	public FaceParser() {
		// Create a SimpleTokenizer that uses a slash as delimiter
		this.slashTokenizer = new Tokenizer();
		this.slashTokenizer.setDelimiter("/");
	}

	public void parse(String model, ModelArrays modelArrays, Group currentGroup, int lineNumber,
			String line) throws ParseException {

		Tokenizer spaceTokenizer = new Tokenizer();
		spaceTokenizer.setText(line.substring(2));

		verifyNumberOfVerticesForFace(model, lineNumber, spaceTokenizer);

		for (int i = 0; i < NUMBER_OF_VERTICES_IN_FACE; i++) {
			String facePartText = spaceTokenizer.next();
			processFacePart(model, modelArrays, currentGroup, lineNumber, facePartText);
		}
	}

	// If the number of vertices is not supported by the face specification a parse exception will
	// be raised. The number of used vertices must be 3 because only triangle faces are supported
	private void verifyNumberOfVerticesForFace(String modelName, int lineNumber,
			Tokenizer spaceTokenizer) throws ParseException {
		int faces = spaceTokenizer.delimOccurCount() + 1;

		if (faces != NUMBER_OF_VERTICES_IN_FACE) {
			throw new ParseException(modelName, lineNumber, "only triangle faces are supported");
		}
	}

	private FacePart extractFacePart(String text, String modelName, int lineNumber)
			throws ParseException {
		this.slashTokenizer.setText(text);

		int vertexCount = this.slashTokenizer.delimOccurCount() + 1;
		FacePart result = new FacePart();

		if (vertexCount == 2) {
			// The face may have vertex and texture only, but it will not be supported now.
			// TODO: Make the full face support - http://en.wikipedia.org/wiki/Wavefront_.obj_file
			throw new ParseException(modelName, lineNumber, "Vertex normal is necessary.");
		} else if (vertexCount == 3) {
			result.vertex = this.slashTokenizer.nextAsInteger();
			result.texture = this.slashTokenizer.nextAsInteger();
			result.normal = this.slashTokenizer.nextAsInteger();
		} else {
			throw new ParseException(modelName, lineNumber,
					"Face part needs to reference vertex, normal and texture coordinate.");
		}

		return result;
	}

	private void addToGroup(int index, ArrayList<float[]> originalList, ArrayList<Float> destList)
			throws Exception {
		float[] vec = originalList.get(index);

		if (vec == null) {
			throw new Exception();
		}

		for (int j = 0; j < vec.length; j++) {
			destList.add(vec[j]);
		}
	}

	private void processFacePart(String modelName, ModelArrays arrays, Group group, int lineNumber,
			String facePartText) throws ParseException {
		FacePart facePart = extractFacePart(facePartText, modelName, lineNumber);

		try {
			addToGroup(facePart.vertex, arrays.vertices, group.groupVertices);
			addToGroup(facePart.normal, arrays.normals, group.groupNormals);

			if (facePart.texture != -1) {
				// in case there is a texture on the face
				addToGroup(facePart.texture, arrays.texcoords, group.groupTexcoords);
			}
		} catch (Exception e) {
			throw new ParseException(modelName, lineNumber, "Could not find reference.");
		}
	}
}
