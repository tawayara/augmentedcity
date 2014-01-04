package com.tawayara.augmentedcity.renderer.parser.obj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class Util {
	
	private static final Pattern trimWhiteSpaces = Pattern.compile("[\\s]+");
	private static final Pattern removeInlineComments = Pattern.compile("#");
	private static final Pattern splitBySpace = Pattern.compile(" ");

	
	/**
	 * returns a canonical line of a obj or mtl file.
	 * e.g. it removes multiple whitespaces or comments from the given string.
	 * @param line
	 * @return
	 */
	public static final String getCanonicalLine(String line) {
		line = trimWhiteSpaces.matcher(line).replaceAll(" ");
		if(line.contains("#")) {
			String[] parts = removeInlineComments.split(line);
			if(parts.length > 0)
				line = parts[0];//remove inline comments
		}
		return line;
	}
	public static String[] splitBySpace(String str) {
		return splitBySpace.split(str);
	}
	
	/**
	 * Trims down obj files, so that they may be parsed faster later on.
	 * Remove uneccessary whitespaces, comments etc.
	 * @param in stream to be trimmed
	 * @param out the resulting trimmed stream
	 */
	public static void trim(BufferedReader in, BufferedWriter out) throws IOException {
		String line;
		out.write("#trimmed\n");
		for (line = in.readLine(); 
		line != null; 
		line = in.readLine()) {
			line = getCanonicalLine(line);
			if(line.length()>0) {
				out.write(line.trim());
				out.write('\n');
			}
		}
		in.close();
		out.close();
	}
	
    public final static List<String> fastSplit(final String text, char separator, final boolean emptyStrings) {
        final List<String> result = new ArrayList<String>();
        
        if (text != null && text.length() > 0) {
            int index1 = 0;
            int index2 = text.indexOf(separator);
            while (index2 >= 0) {
                String token = text.substring(index1, index2);
                result.add(token);
                index1 = index2 + 1;
                index2 = text.indexOf(separator, index1);
            }
            
            if (index1 < text.length() - 1) {
                result.add(text.substring(index1));
            }
        }//else: input unavailable
        
        return result;
    }
	
}