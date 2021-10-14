package de.ollie.classplanter;

import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.EnumDeclaration;
import de.ollie.blueprints.codereader.java.model.InterfaceDeclaration;
import de.ollie.blueprints.codereader.java.model.TypeDeclaration;

/**
 * @author ollie (12.10.2021)
 */
public class SuperInterfaceAgent {

	/**
	 * @param typeDeclaration The type declaration whose super interface names are to return.
	 * @return The super interface names of the passed type declaration or an empty string array if no interfaces are
	 *         implemented.
	 */
	public String[] getSuperInterfaceNames(TypeDeclaration typeDeclaration) {
		if (typeDeclaration instanceof ClassDeclaration) {
			return ((ClassDeclaration) typeDeclaration).getImplementedInterfaceNames().toArray(new String[0]);
		} else if (typeDeclaration instanceof EnumDeclaration) {
			return ((EnumDeclaration) typeDeclaration).getImplementedInterfaceNames().toArray(new String[0]);
		} else if (typeDeclaration instanceof InterfaceDeclaration) {
			return ((InterfaceDeclaration) typeDeclaration).getSuperInterfaceNames().toArray(new String[0]);
		}
		return new String[0];
	}

}