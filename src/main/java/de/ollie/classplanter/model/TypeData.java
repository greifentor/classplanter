package de.ollie.classplanter.model;

import static de.ollie.utils.Str.hasContent;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Generated
public class TypeData {

	public enum Type {
		ABSTRACT_CLASS,
		CLASS,
		ENUM,
		INTERFACE,
		REFERENCED,
		UNKNOWN;
	}

	private String className;
	private List<MemberData> members = new ArrayList<>();
	private String packageName;
	private String superClassName;
	private List<String> stereotypes = new ArrayList<>();
	private List<String> superInterfaceNames = new ArrayList<>();
	private Type type;

	public TypeData addStereotypeNames(String... stereotypeNames) {
		for (String stereotypeName : stereotypeNames) {
			this.stereotypes.add(stereotypeName);
		}
		return this;
	}

	public TypeData addSuperInterfaceNames(String... superInterfaceNames) {
		for (String superInterfaceName : superInterfaceNames) {
			this.superInterfaceNames.add(superInterfaceName);
		}
		return this;
	}

	public String getQualifiedName() {
		if (!hasContent(className)) {
			return "";
		}
		return (hasContent(packageName) ? packageName + "." : "") + className;
	}

}