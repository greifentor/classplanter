package de.ollie.blueprints.codereader.java.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for enum declaration data.
 *
 * @author ollie (14.10.2021)
 */
@Accessors(chain = true)
@Data
@Generated
public class EnumDeclaration implements TypeDeclaration {

	private List<Annotation> annotations = new ArrayList<>();
	private List<FieldDeclaration> fields = new ArrayList<>();
	private List<String> implementedInterfaceNames = new ArrayList<>();
	private List<MethodDeclaration> methods = new ArrayList<>();
	private List<Modifier> modifiers = new ArrayList<>();
	private String name;
	private String superClassName;

	public EnumDeclaration addAnnotations(Annotation... annotations) {
		for (Annotation annotation : annotations) {
			this.annotations.add(annotation);
		}
		return this;
	}

	public EnumDeclaration addFields(FieldDeclaration... fields) {
		for (FieldDeclaration field : fields) {
			this.fields.add(field);
		}
		return this;
	}

	public EnumDeclaration addImplementedInterfaceNames(String... implementedInterfaceNames) {
		for (String implementedInterfaceName : implementedInterfaceNames) {
			this.implementedInterfaceNames.add(implementedInterfaceName);
		}
		return this;
	}

	public EnumDeclaration addMethods(MethodDeclaration... methods) {
		for (MethodDeclaration method : methods) {
			this.methods.add(method);
		}
		return this;
	}

	public EnumDeclaration addModifiers(Modifier... modifiers) {
		for (Modifier modifier : modifiers) {
			this.modifiers.add(modifier);
		}
		return this;
	}

}