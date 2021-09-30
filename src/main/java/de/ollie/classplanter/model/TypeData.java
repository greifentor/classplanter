package de.ollie.classplanter.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class TypeData {

	public enum Type {
		CLASS,
		ENUM,
		INTERFACE,
		UNKNOWN;
	}

	private String className;
	private String packageName;
	private Type type;

}