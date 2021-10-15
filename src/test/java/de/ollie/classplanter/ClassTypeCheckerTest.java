package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClassTypeCheckerTest {

	@InjectMocks
	private ClassTypeChecker unitUnderTest;

	@Nested
	class TestsOfMethod_isClassType_FieldDeclaration {

		@Test
		void passNullValue_returnsFalse() {
			assertFalse(unitUnderTest.isClassType(null));
		}

		@ParameterizedTest
		@CsvSource(value = { "boolean", "byte", "char", "double", "float", "int", "long", "short" })
		void passFieldDeclarationWithSimpleTypeName_returnsFalse(String typeName) {
			assertFalse(unitUnderTest.isClassType(typeName));
		}

		@ParameterizedTest
		@CsvSource(value = { "Boolean", "Byte", "Character", "Double", "Float", "Integer", "Long", "Short", "String" })
		void passFieldDeclarationWithWrapperTypeName_returnsFalse(String typeName) {
			assertFalse(unitUnderTest.isClassType(typeName));
		}

		@ParameterizedTest
		@CsvSource(value = { "AClass", "JFrame" })
		void passFieldDeclarationWithClassTypeName_returnsTrue(String typeName) {
			assertTrue(unitUnderTest.isClassType(typeName));
		}

	}

}