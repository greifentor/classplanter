package de.ollie.classplanter.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.classplanter.yaml.OutputConfigurationContent.PackageMode;

@ExtendWith(MockitoExtension.class)
public class YAMLConfigurationContentFromYamlFileReaderTest {

	@InjectMocks
	private YAMLConfigurationContentFromYamlFileReader unitUnderTest;

	@Nested
	class TestsOfMethod_read_String {

		@Test
		void passANullValue_returnsANullValue() throws Exception {
			assertNull(unitUnderTest.read(null));
		}

		@Test
		void passAValidFileName_readTheConfigurationFileCorrectly() throws Exception {
			// Prepare
			String fileName = "src/test/resources/configuration-test.yml";
			YAMLConfigurationContent expected = new YAMLConfigurationContent()
					.setInput(
							new InputConfigurationContent()
									.setExplicitClasses(List.of("de.ollie.classplanter.AClass", "BClass"))
									.setExplicitPackages(
											List.of("de.ollie.classplanter", "de.ollie.classplanter.model")))
					.setOutput(
							new OutputConfigurationContent()
									.setPackageMode(PackageMode.FLAT)
									.setUniteEqualAssociations(true));
			// Run
			YAMLConfigurationContent returned = unitUnderTest.read(fileName);
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void passAValidFileNameOfAFileWithPartialSettings_readTheConfigurationFileCorrectly() throws Exception {
			// Prepare
			String fileName = "src/test/resources/configuration-partial-test.yml";
			YAMLConfigurationContent expected = new YAMLConfigurationContent()
					.setOutput(new OutputConfigurationContent().setPackageMode(PackageMode.FLAT));
			// Run
			YAMLConfigurationContent returned = unitUnderTest.read(fileName);
			// Check
			assertEquals(expected, returned);
		}

	}

}