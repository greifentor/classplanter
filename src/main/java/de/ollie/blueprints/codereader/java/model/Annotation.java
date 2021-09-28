package de.ollie.blueprints.codereader.java.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for annotation data.
 *
 * @author ollie (13.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class Annotation {

	private String name;
	private String value;
	private Map<String, String> elementValues = new HashMap<>();

	public Annotation addElementValues(ElementValuePair... elementValues) {
		for (ElementValuePair elementValue : elementValues) {
			this.elementValues.put(elementValue.getKey(), elementValue.getValue());
		}
		return this;
	}

}