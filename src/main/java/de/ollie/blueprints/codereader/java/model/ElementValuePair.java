package de.ollie.blueprints.codereader.java.model;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for element value pairs.
 *
 * @author ollie (13.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class ElementValuePair {

	private String key;
	private String value;

}