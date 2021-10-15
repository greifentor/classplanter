package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.classplanter.model.MemberData.Visibility;

@ExtendWith(MockitoExtension.class)
public class VisibilityToPlantUMLConverterTest {

	@InjectMocks
	private VisibilityToPlantUMLConverter unitUnderTest;

	@Nested
	class TestsOfMethod_getPlantUMLString_Visibility {

		@Test
		void passANullValue_returnsANullValue() {
			assertNull(unitUnderTest.getPlantUMLString(null));
		}

		@ParameterizedTest
		@CsvSource(value = { "PUBLIC,+", "PROTECTED,#", "PACKAGE_PRIVATE,~", "PRIVATE,-" })
		void passAVisibility_returnsTheCorrectString(Visibility visibility, String expected) {
			assertEquals(expected, unitUnderTest.getPlantUMLString(visibility));
		}

	}

}