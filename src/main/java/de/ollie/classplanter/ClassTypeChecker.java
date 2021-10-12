package de.ollie.classplanter;

import java.util.List;

import de.ollie.blueprints.codereader.java.model.FieldDeclaration;

/**
 * @author ollie (12.10.2021)
 */
public class ClassTypeChecker {

	private static final List<String> SIMPLE_TYPE_NAMES = List
			.of(
					"boolean",
					"Boolean",
					"byte",
					"Byte",
					"char",
					"Character",
					"double",
					"Double",
					"float",
					"Float",
					"int",
					"Integer",
					"long",
					"Long",
					"short",
					"Short",
					"String");

	/**
	 * @param typeName The name of the to check.
	 * @return "true" if the passed type name is not one of a simple type.
	 */
	public boolean isClassType(FieldDeclaration fieldDeclaration) {
		return !SIMPLE_TYPE_NAMES.contains(fieldDeclaration.getType());
	}

}