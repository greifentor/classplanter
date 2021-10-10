package de.ollie.classplanter;

import java.util.ArrayList;
import java.util.List;

import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.Modifier;
import de.ollie.blueprints.codereader.java.model.ModifierType;
import de.ollie.blueprints.codereader.java.model.TypeDeclaration;

/**
 * @author ollie (10.10.2021)
 */
public class StereotypeReader {

	public static final String UTILITY_CLASS_STEREOTYPE_NAME = "utility class";

	public List<String> getStereotypes(TypeDeclaration typeDeclaration) {
		if (typeDeclaration == null) {
			return null;
		}
		List<String> stereotypes = new ArrayList<>();
		addStereotypeForUtilityClassIfIsOne(stereotypes, typeDeclaration);
		return stereotypes;
	}

	private void addStereotypeForUtilityClassIfIsOne(List<String> stereotypes, TypeDeclaration typeDeclaration) {
		if (typeDeclaration instanceof ClassDeclaration) {
			ClassDeclaration classDeclaration = (ClassDeclaration) typeDeclaration;
			boolean found = classDeclaration
					.getAnnotations()
					.stream()
					.anyMatch(annotation -> annotation.getName().equals("UtilityClass"));
			if (!found && !classDeclaration.getMethods().isEmpty()) {
				found = !classDeclaration
						.getMethods()
						.stream()
						.anyMatch(
								method -> !method.getModifiers().contains(new Modifier().setType(ModifierType.STATIC)));
			}
			if (found) {
				stereotypes.add(UTILITY_CLASS_STEREOTYPE_NAME);
			}
		}
	}

}
