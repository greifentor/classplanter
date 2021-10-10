package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.blueprints.codereader.java.model.Annotation;
import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.MethodDeclaration;
import de.ollie.blueprints.codereader.java.model.Modifier;
import de.ollie.blueprints.codereader.java.model.ModifierType;

@ExtendWith(MockitoExtension.class)
public class StereotypeReaderTest {

	@InjectMocks
	private StereotypeReader unitUnderTest;

	@Nested
	class TestsOfMethod_getStereotypes_TypeDeclaration {

		@Test
		void passANullValue_returnsANullValue() {
			assertNull(unitUnderTest.getStereotypes(null));
		}

		@Test
		void passAClassDeclarationWithNoStereotype_returnsAnEmptyList() {
			assertEquals(List.of(), unitUnderTest.getStereotypes(new ClassDeclaration()));
		}

		@Nested
		class TestsForUtilityClass {

			@Test
			void passAClassDeclarationWithAnUtilityClassAnnotation_returnsAListWithStringUtilityClass() {
				// Prepare
				ClassDeclaration classDeclaration =
						new ClassDeclaration().setAnnotations(List.of(new Annotation().setName("UtilityClass")));
				// Run & Check
				assertEquals(
						List.of(StereotypeReader.UTILITY_CLASS_STEREOTYPE_NAME),
						unitUnderTest.getStereotypes(classDeclaration));
			}

			@Test
			void passAClassDeclarationWithStaticMethodsOnly_returnsAListWithStringUtilityClass() {
				// Prepare
				ClassDeclaration classDeclaration = new ClassDeclaration()
						.setMethods(
								List
										.of(
												new MethodDeclaration()
														.setModifiers(
																List.of(new Modifier().setType(ModifierType.STATIC)))));
				// Run & Check
				assertEquals(
						List.of(StereotypeReader.UTILITY_CLASS_STEREOTYPE_NAME),
						unitUnderTest.getStereotypes(classDeclaration));
			}

			@Test
			void passAClassDeclarationWithStaticAndNonStaticMethods_returnsAnEmptyList() {
				// Prepare
				ClassDeclaration classDeclaration = new ClassDeclaration()
						.setMethods(
								List
										.of(
												new MethodDeclaration()
														.setModifiers(
																List.of(new Modifier().setType(ModifierType.STATIC))),
												new MethodDeclaration().setModifiers(List.of(new Modifier()))));
				// Run & Check
				assertEquals(List.of(), unitUnderTest.getStereotypes(classDeclaration));
			}

		}

	}

}
