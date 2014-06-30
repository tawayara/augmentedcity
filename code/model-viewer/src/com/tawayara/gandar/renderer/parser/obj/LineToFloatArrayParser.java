package com.tawayara.gandar.renderer.parser.obj;


/**
 * Class that aims to parse a given String on an array with float values.
 * 
 */
class LineToFloatArrayParser {

	private String lineStart;
	private int expectedArrayLenght;
	private Tokenizer spaceTokenizer;

	/**
	 * Default constructor of the LineToFloatArrayParser class.
	 * 
	 * @param lineStart
	 *            The String that represents the beginning of the line to be read.
	 * @param expectedArrayLenght
	 *            An integer representing the expected size of the array to be returned as result of
	 *            the parse method.
	 */
	public LineToFloatArrayParser(String lineStart, int expectedArrayLenght) {
		this.lineStart = lineStart;
		this.expectedArrayLenght = expectedArrayLenght;
		this.spaceTokenizer = new Tokenizer();
	}

	/**
	 * Identify if the line can be read by the current parser.
	 * 
	 * @param line
	 *            The line that is being evaluated in order to identify if the current parser can
	 *            read it.
	 * @return A boolean indicating if the parser can read the given line.
	 */
	public boolean canRead(String line) {
		return line.startsWith(this.lineStart);
	}

	/**
	 * Method that aims to parse a given string on an array of float.
	 * 
	 * @param line
	 *            The string to be parsed.
	 * @return The extracted array of float.
	 */
	public float[] parse(String line) {
		float[] result = new float[this.expectedArrayLenght];
		this.spaceTokenizer.setText(getArrayString(line));

		for (int i = 0; i < this.expectedArrayLenght; i++) {
			result[i] = Float.parseFloat(this.spaceTokenizer.next());
		}

		return result;
	}

	// Extract the array string from the given line based on the string that represents the start of
	// the line
	private String getArrayString(String line) {
		return line.substring(this.lineStart.length());
	}

}
