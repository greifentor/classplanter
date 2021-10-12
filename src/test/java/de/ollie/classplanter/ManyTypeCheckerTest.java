package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ManyTypeCheckerTest {

	@InjectMocks
	private ManyTypeChecker unitUnderTest;

	@Nested
	class TestsOfMethod_isManyType_String {

		@Test
		void passANullValue_throwsAnException() {
			assertThrows(NullPointerException.class, () -> unitUnderTest.isManyType(null));
		}

		@Test
		void passANameOfANonManyType_returnsFalse() {
			assertFalse(unitUnderTest.isManyType("String"));
		}

		@ParameterizedTest
		@CsvSource(value = { "List<String>", "Set<String>", "Stack<String>" })
		void passANameOfAManyType_returnsTrue(String typeName) {
			assertTrue(unitUnderTest.isManyType(typeName));
		}

	}

}
