package de.ollie.blueprints.codereader.java.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for the compilation unit information.
 *
 * @author ollie (13.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class CompilationUnit {

	private String packageName;
	private List<ImportDeclaration> importDeclarations = new ArrayList<>();
	private List<TypeDeclaration> typeDeclarations = new ArrayList<>();

	public CompilationUnit addImportDeclarations(ImportDeclaration... importDeclarations) {
		for (ImportDeclaration importDeclaration : importDeclarations) {
			this.importDeclarations.add(importDeclaration);
		}
		return this;
	}

	public CompilationUnit addTypeDeclarations(TypeDeclaration... typeDeclarations) {
		for (TypeDeclaration typeDeclaration : typeDeclarations) {
			this.typeDeclarations.add(typeDeclaration);
		}
		return this;
	}

}