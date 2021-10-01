package de.ollie.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Str {

	/**
	 * Checks if s has any content.
	 * 
	 * @param s The string to check.
	 * @return "true" if the passed string is neither null nor empty. Otherwise "false".
	 */
	public boolean hasContent(String s) {
		return (s != null) && !s.isEmpty();
	}

}