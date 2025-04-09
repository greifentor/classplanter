package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;

@ExtendWith(MockitoExtension.class)
class TypeStorageTest {

	private static final String CLASS_NAME = "AClass";
	private static final String PACKAGE_NAME = "a.package.name";

	@InjectMocks
	private TypeStorage unitUnderTest;

	@Nested
	class addOrUpdate_TypeData {

		@Test
		void throwsAnException_passingANullValueAsTypeData() {
			assertThrows(IllegalArgumentException.class, () -> unitUnderTest.addOrUpdate(null));
		}

		@Test
		void storesThePassedTypeData() {
			// Prepare
			TypeData toStore = new TypeData().setClassName(CLASS_NAME).setPackageName(PACKAGE_NAME);
			// Run
			unitUnderTest.addOrUpdate(toStore);
			// Check
			assertEquals(List.of(toStore), unitUnderTest.getTypes());
		}

		@Test
		void doesNotStoreTheSameTypeDataTwice_whenNotOfUnknownOrReferenced() {
			// Prepare
			TypeData toStore = new TypeData().setClassName(CLASS_NAME).setPackageName(PACKAGE_NAME);
			unitUnderTest.addOrUpdate(toStore);
			// Run
			unitUnderTest.addOrUpdate(toStore);
			// Check
			assertEquals(List.of(toStore), unitUnderTest.getTypes());
		}

		@ParameterizedTest
		@CsvSource({ "REFERENCED,ABSTRACT_CLASS", "REFERENCED,CLASS", "REFERENCED,ENUM", "REFERENCED,INTERFACE",
				"UNKNOWN,ABSTRACT_CLASS", "UNKNOWN,CLASS", "UNKNOWN,ENUM", "UNKNOWN,INTERFACE" })
		void updatesTheTypeData_whenOfUnknownOrReferenced_passingATypeDataNotOfUnknownOrReferenced(Type stored,
				Type passed) {
			// Prepare
			TypeData toStore = new TypeData().setClassName(CLASS_NAME).setPackageName(PACKAGE_NAME).setType(stored);
			unitUnderTest.addOrUpdate(toStore);
			TypeData toPass = new TypeData().setClassName(CLASS_NAME).setPackageName(PACKAGE_NAME).setType(passed);
			// Run
			unitUnderTest.addOrUpdate(toPass);
			// Check
			assertEquals(List.of(toPass), unitUnderTest.getTypes());
			assertEquals(toPass, unitUnderTest.getTypes().get(0));
		}

		@ParameterizedTest
		@CsvSource({ "REFERENCED,ABSTRACT_CLASS", "REFERENCED,CLASS", "REFERENCED,ENUM", "REFERENCED,INTERFACE",
				"UNKNOWN,ABSTRACT_CLASS", "UNKNOWN,CLASS", "UNKNOWN,ENUM", "UNKNOWN,INTERFACE" })
		void doesNotUpdateTheTypeData_whenOfUnknownOrReferenced_passingATypeDataNotOfUnknownOrReferenced(Type passed,
				Type stored) {
			// Prepare
			TypeData toStore = new TypeData().setClassName(CLASS_NAME).setPackageName(PACKAGE_NAME).setType(stored);
			unitUnderTest.addOrUpdate(toStore);
			TypeData toPass = new TypeData().setClassName(CLASS_NAME).setPackageName(PACKAGE_NAME).setType(passed);
			// Run
			unitUnderTest.addOrUpdate(toPass);
			// Check
			assertEquals(List.of(toStore), unitUnderTest.getTypes());
			assertEquals(toStore, unitUnderTest.getTypes().get(0));
		}

	}

	@Nested
	class getTypes {

		@Test
		void differentCallsReturnDifferentLists() {
			// Prepare
			TypeData toStore = new TypeData().setClassName(CLASS_NAME).setPackageName(PACKAGE_NAME);
			unitUnderTest.addOrUpdate(toStore);
			// Run
			assertNotSame(unitUnderTest.getTypes(), unitUnderTest.getTypes());
		}
	}

}