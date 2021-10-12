package de.ollie.classplanter;

import java.util.List;

/**
 * @author ollie (12.10.2021)
 */
public class ManyTypeChecker {

	private static final List<String> MANY_TYPE_NAMES = List.of("List<", "Set<", "Stack<");

	public boolean isManyType(String type) {
		for (String typePrefix : MANY_TYPE_NAMES) {
			if (type.startsWith(typePrefix)) {
				return true;
			}
		}
		return false;
	}

	public String removeManyType(String type) {
		for (String typePrefix : MANY_TYPE_NAMES) {
			if (type.startsWith(typePrefix)) {
				return type.substring(0, type.length() - 1).replace(typePrefix, "");
			}
		}
		return type.replace("<", "").replace(">", "").replace(",", "").replace("[]", "Arr");
	}

}