package de.ollie.classplanter;

import java.util.List;
import java.util.Optional;

import de.ollie.blueprints.codereader.java.model.ImportDeclaration;
import de.ollie.classplanter.model.TypeData;

/**
 * A class which is able to investigate for a types package.
 *
 * @author ollie (04.10.2021)
 */
public class PackageAgent {

	/**
	 * Investigates for a package name matching to the passed type name.
	 *
	 * @param typeName                   The type whose package is to find.
	 * @param compilationUnitMembers     The types which are members of the current compilation unit.
	 * @param compilationUnitPackageName The name of the package of the current compilation unit.
	 * @param imports                    The imports of the compilation unit which the type is a part from.
	 * @return An optional with the package name for the type or an empty optional, if no package name could be found
	 *         for the type.
	 */
	public Optional<String> findPackageNameForType(String typeName, List<TypeData> compilationUnitMembers,
			String compilationUnitPackageName, List<ImportDeclaration> imports) {
		return processForCompilationUnitMembers(typeName, compilationUnitMembers, compilationUnitPackageName)
				.or(() -> processForSingleTypeImports(typeName, imports));
	}

	private Optional<String> processForCompilationUnitMembers(String typeName, List<TypeData> compilationUnitMembers,
			String compilationUnitPackageName) {
		return compilationUnitMembers
				.stream()
				.filter(compilationUnitMember -> compilationUnitMember.getClassName().equals(typeName))
				.map(compilationUnitMember -> compilationUnitPackageName)
				.findFirst();
	}

	private Optional<String> processForSingleTypeImports(String typeName, List<ImportDeclaration> imports) {
		return imports
				.stream()
				.filter(ImportDeclaration::isSingleTypeImport)
				.filter(importDeclaration -> importDeclaration.getQualifiedName().endsWith("." + typeName))
				.map(importDeclaration -> importDeclaration.getQualifiedName().replace("." + typeName, ""))
				.findFirst();
	}

}