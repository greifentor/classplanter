package de.ollie.classplanter.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AssociationData {

	public enum AssociationType {
		MANY_TO_ONE,
		ONE_TO_ONE;
	}

	private ClassKeyData from;
	private ClassKeyData to;
	private AssociationType type = AssociationType.ONE_TO_ONE;

}