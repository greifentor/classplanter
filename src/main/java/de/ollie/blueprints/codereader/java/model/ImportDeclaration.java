package de.ollie.blueprints.codereader.java.model;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for import declaration data.
 *
 * @author ollie (13.04.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class ImportDeclaration {

	private String importedObject;
	private String qualifiedName;
	private boolean staticImport = false;
	private boolean singleTypeImport = true;

}