package de.ollie.classplanter.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ClassKeyData {

	private String className;
	private String packageName;

}