package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ClassPlanterTest {

	@Nested
	class TestsOfMethod_main_String_Arr {

		@Test
		void passParametersForOneClass_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/SimpleClass-Empty.plantuml"));
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/simple-class",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParameterForMoreThanOneClassInOneFolder_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/SimpleClasses-Empty.plantuml"));
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/simple-classes",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForOneInterface_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/SimpleInterface-Empty.plantuml"));
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/simple-interface",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForImplementedInterfaces_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/InterfaceImplementations.plantuml"));
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/interface-implementations",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForASimpleAssociation_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/SimpleAssociation.plantuml"));
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/simple-association",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForAManyToOneAssociation_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/ManyToOneAssociation.plantuml"));
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/many-to-one-association",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForAClassDiagramWithPackageModeFLAT_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected = Files
					.readString(Path.of("src/test/resources/testresults/ClassDiagramWithPackageModeFLAT.plantuml"));
			System.setProperty("classplanter.output.packagemode", "FLAT");
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/class-diagram-package-mode-FLAT",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.setProperty("classplanter.output.packagemode", "NONE");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersWithExplicitPackageInclusion_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/ExplicitPackageInclusion.plantuml"));
			System.setProperty("classplanter.input.include.packages", "a.pack.age.one,a.pack.age.three");
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/explicit-package-inclusion",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.clearProperty("classplanter.input.include.packages");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

	}

}
