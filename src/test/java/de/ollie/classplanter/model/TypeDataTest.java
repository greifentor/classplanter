package de.ollie.classplanter.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TypeDataTest {

	private static final String CLASS_NAME = "ClassName";
	private static final String PACKAGE_NAME = "PackageName";

	@InjectMocks
	private TypeData unitUnderTest;

	@Nested
	class TestsOfMethod_getQualifiedName {

		@Test
		void neitherNameNorPackageSet_returnsAnEmptyString() {
			assertEquals("", unitUnderTest.getQualifiedName());
		}

		@Test
		void nameSetPackagePackageNot_returnsAStringWithNameOnly() {
			// Prepare
			unitUnderTest.setClassName(CLASS_NAME);
			// Run & Check
			assertEquals(CLASS_NAME, unitUnderTest.getQualifiedName());
		}

		@Test
		void packageOnlySet_returnsAnEmptyString() {
			// Prepare
			unitUnderTest.setPackageName(PACKAGE_NAME);
			// Run & Check
			assertEquals("", unitUnderTest.getQualifiedName());
		}

		@Test
		void classNameAndPackageName_returnsACorrectQualifiedName() {
			// Prepare
			unitUnderTest.setClassName(CLASS_NAME);
			unitUnderTest.setPackageName(PACKAGE_NAME);
			// Run & Check
			assertEquals(PACKAGE_NAME + "." + CLASS_NAME, unitUnderTest.getQualifiedName());
		}

	}

}