package de.ollie.classplanter.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * @author ollie (15.10.2021)
 */
@Accessors(chain = true)
@Data
@Generated
public class MemberData {

	public enum Visibility {
		PUBLIC,
		PROTECTED,
		PACKAGE_PRIVATE,
		PRIVATE,
		NONE;
	}

	public enum Modifier {
		FINAL,
		STATIC
	}

	private String name;
	private String type;
	private Set<Modifier> modifiers = new HashSet<>();
	private Visibility visibility = Visibility.NONE;

	public boolean isConstant() {
		return modifiers.contains(Modifier.FINAL) && modifiers.contains(Modifier.STATIC);
	}

}