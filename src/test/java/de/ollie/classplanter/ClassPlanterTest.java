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
			System.setProperty("classplanter.output.packageMode", "FLAT");
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
			System.setProperty("classplanter.output.packageMode", "NONE");
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
			System.setProperty("classplanter.input.includePackages", "a.pack.age.one,a.pack.age.three");
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
			System.clearProperty("classplanter.input.includePackages");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersWithUnitEqualAssociationSet_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/UnitedEqualAssociations.plantuml"));
			System.setProperty("classplanter.output.uniteEqualAssociations", "true");
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/united-equal-associations",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.clearProperty("classplanter.output.uniteEqualAssociations");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForAConfigurationFileWithUnitEqualAssociationSet_createsACorrectPlantUMLFile(
				@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/UnitedEqualAssociations.plantuml"));
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-cnf",
										"src/test/resources/configuration-classplanter-test.yml",
										"-sf",
										"src/test/resources/testsources/united-equal-associations",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForAnnotatedUtilityClasses_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/UtilityClass-Annotation.plantuml"));
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/utility-class-annotated",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForUtilityClassesWithOnlyStaticMethods_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/UtilityClass-StaticMethodsOnly.plantuml"));
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/utility-class-static-methods-only",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForMoreThanOneSourceFolder_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/MoreThanOneSourceFolder.plantuml"));
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/more-than-one-source-folder/folder-1,"
												+ "src/test/resources/testsources/more-than-one-source-folder/folder-2",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForLimitationExplicitlyByClassName_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/ClassesLimitedExplicitlyByName.plantuml"));
			System.setProperty("classplanter.input.explicitClassNames", "a.pack.age.AClass,DClass");
			System.setProperty("classplanter.output.packageMode", "FLAT");
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/classes-limited-explicitly-by-names",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			System.setProperty("classplanter.output.packageMode", "NONE");
			System.clearProperty("classplanter.input.explicitClassNames");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForAEnumClass_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/SimpleEnum.plantuml"));
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/simple-enum",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForShowingMemberFields_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/ClassesWithMembers.plantuml"));
			System.setProperty("classplanter.output.showMembers", "true");
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/classes-with-members",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			System.clearProperty("classplanter.output.showMembers");
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForAEnumClassWithMembers_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/EnumWithMembers.plantuml"));
			System.setProperty("classplanter.output.showMembers", "true");
			// Run
			try {
				ClassPlanter
						.main(
								new String[] {
										"-sf",
										"src/test/resources/testsources/simple-enum",
										"-tf",
										tempDir.toString() + "/result.plantuml" });
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.clearProperty("classplanter.output.showMembers");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParameterForExclusionByClassName_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files
					.readString(Path.of("src/test/resources/testresults/SimpleClasses-ExcludeByClassName.plantuml"));
			System.setProperty("classplanter.output.excludeByClassName", "SimpleClass2,SimpleClass4");
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/simple-classes",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			System.clearProperty("classplanter.output.excludeByClassName");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParameterForExclusionByClassNameNoHits_createsACorrectPlantUMLFile(@TempDir Path tempDir)
				throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/SimpleClasses-Empty.plantuml"));
			System.setProperty("classplanter.output.excludeByClassName", "SimpleClass22,SimpleClass");
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/simple-classes",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			System.clearProperty("classplanter.output.excludeByClassName");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForASimpleAssociationReferencedClassExcluded_createsACorrectPlantUMLFile(
				@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files
					.readString(
							Path
									.of(
											"src/test/resources/testresults/SimpleAssociation-ReferencedClassExcluded.plantuml"));
			System.setProperty("classplanter.output.excludeByClassName", "BClass");
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/exclude-association",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			System.clearProperty("classplanter.output.excludeByClassName");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForASimpleAssociationReferencingClassExcluded_createsACorrectPlantUMLFile(
				@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files
					.readString(
							Path
									.of(
											"src/test/resources/testresults/SimpleAssociation-ReferencingClassExcluded.plantuml"));
			System.setProperty("classplanter.output.excludeByClassName", "AClass");
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/exclude-association",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			System.clearProperty("classplanter.output.excludeByClassName");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passParametersForIgnoreOrphans_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected = Files.readString(Path.of("src/test/resources/testresults/IgnoreOrphans.plantuml"));
			System.setProperty("classplanter.output.ignoreOrphans", "true");
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testsources/ignore-orphans",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			System.clearProperty("classplanter.output.ignoreOrphans");
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

	}

}
