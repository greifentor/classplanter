package de.ollie.classplanter.model;

import static de.ollie.utils.Str.hasContent;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class TypeData {

	public enum Type {
		ABSTRACT_CLASS,
		CLASS,
		ENUM,
		INTERFACE,
		UNKNOWN;
	}

	private String className;
	private String packageName;
	private Type type;

	public String getQualifiedName() {
		if (!hasContent(className)) {
			return "";
		}
		return (hasContent(packageName) ? packageName + "." : "") + className;
	}

}