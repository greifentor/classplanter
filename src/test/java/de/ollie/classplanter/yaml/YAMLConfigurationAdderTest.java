package de.ollie.classplanter.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.classplanter.Configuration;

@ExtendWith(MockitoExtension.class)
public class YAMLConfigurationAdderTest {

	private static final String EXPLICIT_PACKAGE_NAME_0 = "explicitPackageName0";
	private static final String EXPLICIT_PACKAGE_NAME_1 = "explicitPackageName1";
	private static final OutputConfigurationContent.PackageMode PACKAGE_MODE =
			OutputConfigurationContent.PackageMode.FLAT;
	private static final boolean UNITE_REQUAL_ASSOCIATIONS = true;

	@InjectMocks
	private YAMLConfigurationAdder unitUnderTest;

	@Nested
	class TestsOfMethod_add_Configuration_YAMLConfigurationContent {

		@Test
		void passANullValueAsConfiguration_throwsAnException() {
			assertThrows(IllegalArgumentException.class, () -> unitUnderTest.add(null, new YAMLConfigurationContent()));
		}

		@Test
		void passANullValueAsYAMLConfigurationContent_throwsAnException() {
			assertThrows(IllegalArgumentException.class, () -> unitUnderTest.add(new Configuration(), null));
		}

		@Test
		void passAnEmptyConfigurationAndAFullyLoadedYAMLConfigurationContent_setsTheValuesOfTheYAMLConfigurationContentToTheConfiguration() {
			// Prepare
			YAMLConfigurationContent yamlConfigurationContent = new YAMLConfigurationContent()
					.setInput(
							new InputConfigurationContent()
									.setExplicitPackages(List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
					.setOutput(
							new OutputConfigurationContent()
									.setPackageMode(PACKAGE_MODE)
									.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS));
			Configuration expected = new Configuration()
					.setExplicitPackages(new ArrayList<>(List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
					.setPackageMode(Configuration.PackageMode.FLAT)
					.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS);
			// Run
			Configuration returned = unitUnderTest.add(new Configuration(), yamlConfigurationContent);
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passAFullyLoadedConfigurationAndAnEmptyYAMLConfigurationContent_doesNotChangeTheValuesOfTheConfiguration() {
			// Prepare
			YAMLConfigurationContent yamlConfigurationContent = new YAMLConfigurationContent();
			Configuration expected = new Configuration()
					.setExplicitPackages(new ArrayList<>(List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
					.setPackageMode(Configuration.PackageMode.FLAT)
					.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS);
			// Run
			Configuration returned =
					unitUnderTest
							.add(
									new Configuration()
											.setExplicitPackages(
													new ArrayList<>(
															List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
											.setPackageMode(Configuration.PackageMode.FLAT)
											.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS),
									yamlConfigurationContent);
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passAFullyLoadedConfigurationAndAFullyLoadedYAMLConfigurationContent_setsTheValuesOfTheYAMLConfigurationContentToTheConfiguration() {
			// Prepare
			YAMLConfigurationContent yamlConfigurationContent = new YAMLConfigurationContent()
					.setInput(
							new InputConfigurationContent()
									.setExplicitPackages(List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
					.setOutput(
							new OutputConfigurationContent()
									.setPackageMode(PACKAGE_MODE)
									.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS));
			Configuration expected = new Configuration()
					.setExplicitPackages(new ArrayList<>(List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
					.setPackageMode(Configuration.PackageMode.FLAT)
					.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS);
			// Run
			Configuration returned = unitUnderTest
					.add(
							new Configuration()
									.setExplicitPackages(
											new ArrayList<>(
													List.of(EXPLICIT_PACKAGE_NAME_0 + 1, EXPLICIT_PACKAGE_NAME_1 + 1)))
									.setPackageMode(Configuration.PackageMode.NONE)
									.setUniteEqualAssociations(!UNITE_REQUAL_ASSOCIATIONS),
							yamlConfigurationContent);
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passAFullyLoadedConfigurationAndAPartialLoadedYAMLConfigurationContent_setsTheValuesOfTheYAMLConfigurationContentToTheConfiguration() {
			// Prepare
			YAMLConfigurationContent yamlConfigurationContent = new YAMLConfigurationContent()
					.setOutput(new OutputConfigurationContent().setPackageMode(PACKAGE_MODE));
			Configuration expected = new Configuration()
					.setExplicitPackages(new ArrayList<>(List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
					.setPackageMode(Configuration.PackageMode.FLAT)
					.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS);
			// Run
			Configuration returned =
					unitUnderTest
							.add(
									new Configuration()
											.setExplicitPackages(
													new ArrayList<>(
															List.of(EXPLICIT_PACKAGE_NAME_0, EXPLICIT_PACKAGE_NAME_1)))
											.setPackageMode(Configuration.PackageMode.FLAT)
											.setUniteEqualAssociations(UNITE_REQUAL_ASSOCIATIONS),
									yamlConfigurationContent);
			// Check
			assertEquals(expected, returned);
		}

	}

}