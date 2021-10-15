package de.ollie.classplanter.model;

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
		PRIVATE;
	}

	private String name;
	private String type;
	private Visibility visibility;

}