package de.ollie.blueprints.codereader.java.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for formal parameter data.
 *
 * @author ollie (26.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class FormalParameter {

	private List<Annotation> annotations = new ArrayList<>();
	private String name;
	private String type;

	public FormalParameter addAnnotations(Annotation... annotations) {
		for (Annotation annotation : annotations) {
			this.annotations.add(annotation);
		}
		return this;
	}

}