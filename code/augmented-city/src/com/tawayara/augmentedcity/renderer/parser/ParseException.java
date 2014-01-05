package com.tawayara.augmentedcity.renderer.parser;

/**
 * Indicates that something got wrong during the parse of some 3D model specification.
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * The constructor of the ParseException class.
	 * 
	 * @param model
	 *            The name of the model that raised the error during parse.
	 * @param lineNumber
	 *            The line on which the error happened.
	 * @param msg
	 *            A detailed message of the error.
	 */
	public ParseException(String model, int lineNumber, String msg) {
		super("Parse error for model " + model + " on line " + lineNumber + ": " + msg);
	}
}
