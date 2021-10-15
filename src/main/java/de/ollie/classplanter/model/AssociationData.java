package de.ollie.classplanter.model;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Generated
public class AssociationData {

	public enum AssociationType {
		MANY_TO_ONE,
		ONE_TO_ONE;
	}

	private String fieldName;
	private ClassKeyData from;
	private ClassKeyData to;
	private AssociationType type = AssociationType.ONE_TO_ONE;

}