package com.tawayara.augmentedcity.renderer.parser.obj;

/**
 * Class that aims to receive a text and navigate on it's content. The navigation is made retrieving
 * tokens based on a given separator.
 */
class Tokenizer {
	private String text;
	private String delimiter;
	private int delimiterLength;
	private int startPosition;
	private int nextDelimiterPosition;

	public Tokenizer() {
		this.text = "";
		this.startPosition = 0;
		this.nextDelimiterPosition = 0;

		// Use blank space as the default delimiter.
		this.setDelimiter(" ");
	}

	/**
	 * Change the text to be used in the tokenizer and move it to the start position.
	 * 
	 * @param text
	 *            The text to be used.
	 */
	public final void setText(String text) {
		this.text = text;
		this.startPosition = 0;
		this.nextDelimiterPosition = text.indexOf(this.delimiter);
	}

	/**
	 * Change the text to be used as delimiter. The default one is a blank space.
	 * 
	 * @param delimiter
	 *            The text to be used as delimiter.
	 */
	public final void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		this.delimiterLength = this.delimiter.length();
	}

	/**
	 * Verify if there is more token to be read.
	 * 
	 * @return A boolean indicating if there is a next token to be read.
	 */
	public final boolean hasNext() {
		return this.nextDelimiterPosition >= 0;
	}

	/**
	 * Navigate to the next token and return it's value as String.
	 * 
	 * @return The next token value as String.
	 */
	public final String next() {
		// Verify if there is a next delimiter to be read.
		if (this.nextDelimiterPosition >= 0) {
			// Retrieve the text from the start position to the next delimiter position. And update
			// the current values to be used in the next iteration.
			String result = this.text.substring(this.startPosition, this.nextDelimiterPosition);
			this.startPosition = this.nextDelimiterPosition + 1;
			this.nextDelimiterPosition = this.text.indexOf(this.delimiter, this.startPosition);
			return result;
		} else {
			// If there is no next delimiter, retrieve the last part of the text starting from the
			// start position.
			return this.text.substring(this.startPosition);
		}
	}

	/**
	 * Navigate to the next token and return it's value as integer. If the value can not be read,
	 * this method will return -1 as the default value.
	 * 
	 * @return The next token value as integer.
	 */
	public final int nextAsInteger() {
		try {
			return Integer.parseInt(next()) - 1;
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Retrieve the number of occurrences of the delimiter in the current text.
	 * 
	 * @return The number of occurrences of the delimiter in the current text.
	 */
	public final int delimOccurCount() {
		int result = 0;
		if (this.delimiterLength > 0) {
			int start = this.text.indexOf(this.delimiter);
			while (start != -1) {
				result++;
				start = this.text.indexOf(this.delimiter, start + this.delimiterLength);
			}
		}
		return result;
	}

}
