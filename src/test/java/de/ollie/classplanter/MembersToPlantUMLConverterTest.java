package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.classplanter.model.MemberData;
import de.ollie.classplanter.model.MemberData.Visibility;
import de.ollie.classplanter.model.TypeData;

@ExtendWith(MockitoExtension.class)
class MembersToPlantUMLConverterTest {

	private static final String CLASS_NAME = "ClassName";
	private static final String MEMBER_NAME = "MemberName";
	private static final String TYPE_NAME = "Long"; // OLI: Must be a simple or wrapper type name.

	@Mock
	private Configuration configuration;

	@Spy
	private ClassTypeChecker classTypeChecker = new ClassTypeChecker();

	@Spy
	private VisibilityToPlantUMLConverter visibilityToPlantUMLConverter = new VisibilityToPlantUMLConverter();

	@InjectMocks
	private MembersToPlantUMLConverter unitUnderTest;

	@Nested
	class TestsOfMethod_createMemberCode_TypeData_Configuration_boolean_ListTypeData {

		@Test
		void returnsACorrectString_passingTypeDataWithAStaticFinalMember() {
			// Prepare
			String expected = "	+ {static} final " + MEMBER_NAME + " : " + TYPE_NAME + "\n";
			TypeData passed = new TypeData().setClassName(CLASS_NAME)
					.setMembers(List.of(new MemberData().setName(MEMBER_NAME)
							.setType(TYPE_NAME)
							.setVisibility(Visibility.PUBLIC)
							.setModifiers(Set.of(MemberData.Modifier.FINAL, MemberData.Modifier.STATIC))));
			when(configuration.isShowMembers()).thenReturn(true);
			// Run
			String returned = unitUnderTest.createMemberCode(passed, configuration, false, List.of());
			// Check
			assertEquals(expected, returned);
		}

		@Test
		void returnsAnEmptyString_passingTypeDataWithAStaticFinalMemberWithConfigurationIgnoreConstants() {
			// Prepare
			TypeData passed = new TypeData().setClassName(CLASS_NAME)
					.setMembers(List.of(new MemberData().setName(MEMBER_NAME)
							.setType(TYPE_NAME)
							.setVisibility(Visibility.PUBLIC)
							.setModifiers(Set.of(MemberData.Modifier.FINAL, MemberData.Modifier.STATIC))));
			when(configuration.isIgnoreConstants()).thenReturn(true);
			when(configuration.isShowMembers()).thenReturn(true);
			// Run
			String returned = unitUnderTest.createMemberCode(passed, configuration, false, List.of());
			// Check
			assertTrue(returned.isEmpty());
		}

	}

}
