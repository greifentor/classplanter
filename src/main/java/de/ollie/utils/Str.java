package de.ollie.utils;

public class Str {

	/**
	 * Checks if s has any content.
	 * 
	 * @param s The string to check.
	 * @return "true" if the passed string is neither null nor empty. Otherwise "false".
	 */
    public static boolean hasContent(String s) {
		return (s != null) && !s.isEmpty();
	}

}