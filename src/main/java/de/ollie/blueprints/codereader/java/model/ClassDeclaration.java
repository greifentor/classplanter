package de.ollie.blueprints.codereader.java.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for class declaration data.
 *
 * @author ollie (13.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class ClassDeclaration implements TypeDeclaration {

	private List<Annotation> annotations = new ArrayList<>();
	private List<FieldDeclaration> fields = new ArrayList<>();
	private List<MethodDeclaration> methods = new ArrayList<>();
	private List<Modifier> modifiers = new ArrayList<>();
	private String name;

	public ClassDeclaration addAnnotations(Annotation... annotations) {
		for (Annotation annotation : annotations) {
			this.annotations.add(annotation);
		}
		return this;
	}

	public ClassDeclaration addFields(FieldDeclaration... fields) {
		for (FieldDeclaration field : fields) {
			this.fields.add(field);
		}
		return this;
	}

	public ClassDeclaration addMethods(MethodDeclaration... methods) {
		for (MethodDeclaration method : methods) {
			this.methods.add(method);
		}
		return this;
	}

	public ClassDeclaration addModifiers(Modifier... modifiers) {
		for (Modifier modifier : modifiers) {
			this.modifiers.add(modifier);
		}
		return this;
	}

}