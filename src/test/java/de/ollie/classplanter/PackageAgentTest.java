package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.blueprints.codereader.java.model.ImportDeclaration;
import de.ollie.classplanter.model.TypeData;

@ExtendWith(MockitoExtension.class)
public class PackageAgentTest {

	private static final String PACKAGE_NAME = "pack.age.name";
	private static final String TYPE_NAME = "TypeName";

	@InjectMocks
	private PackageAgent unitUnderTest;

	@Nested
	class TestOfMethod_findPackageNameForType {

		@Test
		void investigateForATypeWhichIsMemberOfTheCurrentCompilationUnit_returnsThePackageNameOfTheCompilationUnit() {
			// Prepare
			List<ImportDeclaration> imports = List.of();
			List<TypeData> compilationUnitMembers = List.of(new TypeData().setClassName(TYPE_NAME));
			// Run
			String returned = unitUnderTest
					.findPackageNameForType(TYPE_NAME, compilationUnitMembers, PACKAGE_NAME, imports)
					.get();
			// Check
			assertEquals(PACKAGE_NAME, returned);
		}

		@Test
		void investigateForATypeWhichIsImportedDirectly_returnsTheCorrectPackageName() {
			// Prepare
			List<ImportDeclaration> imports = List
					.of(
							new ImportDeclaration()
									.setQualifiedName(PACKAGE_NAME + "." + TYPE_NAME)
									.setSingleTypeImport(true));
			List<TypeData> compilationUnitMembers = List.of();
			// Run
			String returned = unitUnderTest
					.findPackageNameForType(TYPE_NAME, compilationUnitMembers, "an.other.pack.age", imports)
					.get();
			// Check
			assertEquals(PACKAGE_NAME, returned);
		}

	}

}
