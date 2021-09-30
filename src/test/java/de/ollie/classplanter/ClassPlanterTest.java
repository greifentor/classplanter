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
		void passConfigurationForOneClass_createsACorrectPlantUMLFile(@TempDir Path tempDir) throws Exception {
			// Prepare
			String expected =
					Files.readString(Path.of("src/test/resources/testresults/simple-empty/SimpleClass-Empty.plantuml"));
			// Run
			ClassPlanter
					.main(
							new String[] {
									"-sf",
									"src/test/resources/testresults/simple-empty",
									"-tf",
									tempDir.toString() + "/result.plantuml" });
			String returned = Files.readString(Path.of(tempDir.toString(), "result.plantuml"));
			// Check
			assertEquals(expected, returned);
		}

	}

}
