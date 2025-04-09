package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.classplanter.Configuration.PackageMode;
import de.ollie.classplanter.model.MemberData;
import de.ollie.classplanter.model.MemberData.Visibility;
import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;
import de.ollie.fstools.traversal.FileFoundEvent;

@ExtendWith(MockitoExtension.class)
class ClassPlanterFileFoundListenerTest {

	private Configuration configuration;

	private ClassPlanterFileFoundListener unitUnderTest;

	@BeforeEach
	void beforeEach() {
		configuration = new Configuration().setPackageMode(PackageMode.FLAT);
		unitUnderTest = new ClassPlanterFileFoundListener(configuration);
	}

	@Nested
	class fileFound_FileFoundEvent {

		@Test
		void runsCorrectlyWithFilesInOrder() {
			// Prepare
			List<TypeData> expected = List.of(
					new TypeData().setClassName("AClass").setPackageName("a.test.pack.age.one").setType(Type.CLASS),
					new TypeData().setClassName("AnInterface").setPackageName("a.test.pack.age.one")
							.setType(Type.INTERFACE),
					new TypeData().setClassName("BClass").setPackageName("a.test.pack.age.one")
							.setSuperClassName("AClass").setType(Type.CLASS),
					new TypeData().setClassName("CClass").setMembers(List.of(
							new MemberData().setName("bClass").setType("BClass").setVisibility(Visibility.PRIVATE),
							new MemberData().setName("dClass").setType("DClass").setVisibility(Visibility.PRIVATE)))
							.setPackageName("a.test.pack.age.two").setSuperInterfaceNames(List.of("AnInterface"))
							.setType(Type.CLASS),
					new TypeData().setClassName("DClass").setPackageName("an.other.pack.age").setType(Type.REFERENCED));
			String path = "src/test/resources/testsources/class-diagram-package-mode-FLAT/";
			unitUnderTest.fileFound(new FileFoundEvent().setPath(Path.of(path + "CClass.java")));
			unitUnderTest.fileFound(new FileFoundEvent().setPath(Path.of(path + "AClass.java")));
			unitUnderTest.fileFound(new FileFoundEvent().setPath(Path.of(path + "BClass.java")));
			// Run
			List<TypeData> returned = unitUnderTest.getClasses();
			// Check
			assertEquals(expected, returned);
		}

	}

}
