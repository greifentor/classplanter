package de.ollie.classplanter;

import java.util.List;

import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;

/**
 * @author ollie (12.10.2021)
 */
public class ClassTypeChecker {

	private static final List<String> SIMPLE_TYPE_NAMES = List.of("boolean",
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
	public boolean isClassType(String typeName) {
		return typeName != null ? !SIMPLE_TYPE_NAMES.contains(typeName) : false;
	}

	/**
	 * @param typeName      The name of the to check.
	 * @param configuration The configuration of the application.
	 * @param type          A list of types.
	 * @return "true" if the passed type name is not a simple type and not an Enum which should be handled as simple
	 *         type.
	 */
	public boolean isAClassType(String typeName, Configuration configuration, List<TypeData> types) {
		if (isEnumAndShouldBeHandledAsSimpleClass(typeName, configuration, types)) {
			return false;
		}
		return isClassType(typeName);
	}

	private boolean isEnumAndShouldBeHandledAsSimpleClass(String typeName, Configuration configuration,
			List<TypeData> types) {
		return configuration.isHandleEnumsAsSimpleTypes() && isEnumType(typeName, types);
	}

	private boolean isEnumType(String typeName, List<TypeData> types) {
		return types.stream()
				.anyMatch(typeData -> (typeData.getType() == Type.ENUM) && (typeData.getClassName().equals(typeName)));
	}

}