package de.ollie.classplanter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StringListSplitterTest {

	private static final String ELEMENT_0 = "element0";
	private static final String ELEMENT_1 = "element1";
	private static final String ELEMENT_2 = "element2";

	@InjectMocks
	private StringListSplitter unitUnderTest;

	@Nested
	class TestsOfMethod_split_String {

		@Test
		void passNullValue_returnsANullValue() {
			assertNull(unitUnderTest.split(null));
		}

		@Test
		void passAnEmptyString_returnsAnEmptyList() {
			assertEquals(List.of(), unitUnderTest.split(""));
		}

		@Test
		void passAStringWithASingleListEntry_returnsAListWithJustOneElement() {
			assertEquals(List.of(ELEMENT_0), unitUnderTest.split(ELEMENT_0));
		}

		@Test
		void passAStringWithMoreThanOneListEntry_returnsAListWithAllListElements() {
			assertEquals(
					List.of(ELEMENT_0, ELEMENT_1, ELEMENT_2),
					unitUnderTest.split(ELEMENT_0 + "," + ELEMENT_1 + "," + ELEMENT_2));
		}

		@Test
		void passAStringWithMoreThanOneListEntryAndSpaces_returnsAListWithAllListElementsTrimmed() {
			assertEquals(
					List.of(ELEMENT_0, ELEMENT_1, ELEMENT_2),
					unitUnderTest.split("    " + ELEMENT_0 + " ," + ELEMENT_1 + " ,\t  " + ELEMENT_2 + " "));
		}

	}

}