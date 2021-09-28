package de.ollie.blueprints.codereader.java.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container formethod declaration data.
 *
 * @author ollie (26.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class MethodDeclaration {

	private List<Annotation> annotations = new ArrayList<>();
	private List<Modifier> modifiers = new ArrayList<>();
	private String name;
	private List<FormalParameter> formalParameters = new ArrayList<>();
	private String returnType;

	public MethodDeclaration addAnnotations(Annotation... annotations) {
		for (Annotation annotation : annotations) {
			this.annotations.add(annotation);
		}
		return this;
	}

	public MethodDeclaration addFormalParameters(FormalParameter... formalParameters) {
		for (FormalParameter formalParameter : formalParameters) {
			this.formalParameters.add(formalParameter);
		}
		return this;
	}

	public MethodDeclaration addModifiers(Modifier... modifiers) {
		for (Modifier modifier : modifiers) {
			this.modifiers.add(modifier);
		}
		return this;
	}

}