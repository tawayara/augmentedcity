package com.tawayara.gandar.renderer.parser.obj;

import java.util.regex.Pattern;

/**
 * Class that aims to remove unnecessary stuff from a given text. The class will try to remove the
 * characters that do not impact in the 3D model specification (like comments and multiple white
 * spaces).
 */
class LineCleanner {

	private static final Pattern TRIM_WHITE_SPACES = Pattern.compile("[\\s]+"); // NOPMD by tiana on 05/01/14 13:45
	private static final Pattern REMOVE_INLINE_COMMENTS = Pattern.compile("#");

	/**
	 * Removes multiple white spaces and comments from the given string.
	 * 
	 * @param line
	 *            The text to remove the white spaces and comments.
	 * @return The given text without white spaces and comments.
	 */
	public static final String removeMultipleSpacesAndComments(String line) {
		String result = TRIM_WHITE_SPACES.matcher(line).replaceAll(" ");
		if (result.contains("#")) {
			String[] parts = REMOVE_INLINE_COMMENTS.split(result);
			if (parts.length > 0)
				result = parts[0];
		}

		return result.trim();
	}
}
