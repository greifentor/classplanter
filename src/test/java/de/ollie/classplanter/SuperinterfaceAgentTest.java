package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.InterfaceDeclaration;

@ExtendWith(MockitoExtension.class)
public class SuperinterfaceAgentTest {

	private static final String INTERFACE_NAME_0 = "interface name 0";
	private static final String INTERFACE_NAME_1 = "interface name 1";

	@InjectMocks
	private SuperinterfaceAgent unitUnderTest;

	@Nested
	class TestsOfMethod_getSuperInterfaceNames_TypeDeclaration {

		@Test
		void passANullPointer_returnsAnEmptyStringArray() {
			assertArrayEquals(new String[0], unitUnderTest.getSuperInterfaceNames(null));
		}

		@Test
		void passAClassDeclarationWithoutAnySuperInterfaces_returnsAnEmptyStringArray() {
			assertArrayEquals(new String[0], unitUnderTest.getSuperInterfaceNames(new ClassDeclaration()));
		}

		@Test
		void passAClassDeclarationWithImplementatedInterfaces_returnsAStringArrayWithTheImplementedInterfaces() {
			assertArrayEquals(
					new String[] { INTERFACE_NAME_0, INTERFACE_NAME_1 },
					unitUnderTest
							.getSuperInterfaceNames(
									new ClassDeclaration()
											.setImplementedInterfaceNames(
													List.of(INTERFACE_NAME_0, INTERFACE_NAME_1))));
		}

		@Test
		void passAInterfaceDeclarationWithoutAnySuperInterfaces_returnsAnEmptyStringArray() {
			assertArrayEquals(new String[0], unitUnderTest.getSuperInterfaceNames(new InterfaceDeclaration()));
		}

		@Test
		void passAInterfaceDeclarationWithSuperInterfaces_returnsAStringArrayWithTheSuperInterfaceName() {
			assertArrayEquals(
					new String[] { INTERFACE_NAME_0, INTERFACE_NAME_1 },
					unitUnderTest
							.getSuperInterfaceNames(
									new InterfaceDeclaration()
											.setSuperInterfaceNames(List.of(INTERFACE_NAME_0, INTERFACE_NAME_1))));
		}

	}

}