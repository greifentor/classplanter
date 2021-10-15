package de.ollie.classplanter;

import java.util.Map;

import de.ollie.classplanter.model.MemberData.Visibility;

/**
 * @author ollie (15.10.2021)
 */
public class VisibilityToPlantUMLConverter {

	private static final Map<Visibility, String> MAPPING = Map
			.of(
					Visibility.PACKAGE_PRIVATE,
					"~",
					Visibility.PRIVATE,
					"-",
					Visibility.PROTECTED,
					"#",
					Visibility.PUBLIC,
					"+");

	public String getPlantUMLString(Visibility visibility) {
		return visibility == null ? null : MAPPING.getOrDefault(visibility, "?");
	}

}
