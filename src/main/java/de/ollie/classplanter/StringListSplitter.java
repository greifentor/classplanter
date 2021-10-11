package de.ollie.classplanter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author ollie (11.10.2021)
 */
public class StringListSplitter {

	/**
	 * Separates elements of a comma separated string into a string list.
	 * 
	 * @param s The string with a comma separated string content.
	 * @return A list of string with the content of the string.
	 */
	public List<String> split(String s) {
		if (s == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(s, ",");
		List<String> list = new ArrayList<>();
		while (st.hasMoreTokens()) {
			list.add(st.nextToken().trim());
		}
		return list;
	}

}
