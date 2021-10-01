package de.ollie.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StrTest {

	@Nested
	class TestsOfMethod_hasContent_String {

		@Test
		void passANullValue_returnsFalse() {
			assertFalse(Str.hasContent(null));
		}

		@Test
		void passAnEmptyString_returnsFalse() {
			assertFalse(Str.hasContent(""));
		}

		@Test
		void passAStringWithContent_returnsTrue() {
			assertTrue(Str.hasContent(";op"));
		}

	}

}
