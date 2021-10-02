package de.ollie.classplanter.model;

import java.util.List;

import de.ollie.classplanter.model.TypeData.Type;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ClassKeyData {

	private String className;
	private String packageName;

}