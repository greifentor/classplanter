package de.ollie.blueprints.codereader.java.model;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for modifiers.
 *
 * @author ollie (13.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class Modifier {

	public static final Modifier ABSTRACT = new Modifier().setType(ModifierType.ABSTRACT);
	public static final Modifier PRIVATE = new Modifier().setType(ModifierType.PRIVATE);
	public static final Modifier PROTECTED = new Modifier().setType(ModifierType.PROTECTED);
	public static final Modifier PUBLIC = new Modifier().setType(ModifierType.PUBLIC);

	private ModifierType type;

}