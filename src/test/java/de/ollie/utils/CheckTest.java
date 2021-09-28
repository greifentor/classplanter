package de.ollie.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for class "Check".
 *
 * @author ollie
 *
 */
public class CheckTest {

	private static final String MESSAGE = "message";

	@Test
	public void constructor_ThrowsAnUnsupportedOperationException() {
		assertThrows(UnsupportedOperationException.class, () -> new Check());
	}

	@Test
	public void ensure_TrueConditionPassed_DoesNtThrowAnException() {
		try {
			Check.ensure(true, new RuntimeException());
		} catch (RuntimeException e) {
			fail("no exception should thrown.");
		}
	}

	@Test
	public void ensure_FalseConditionPassed_ThrowsThePassedException() {
		RuntimeException e = new RuntimeException();
		try {
			Check.ensure(false, e);
			fail("should throw a runtime exception.");
		} catch (RuntimeException e0) {
			assertSame(e, e0, "thrown exception should be same as the passed one");
		}
	}

	@Test
	public void ensure_TrueConditionAndAStringPassed_DoesNtThrowAnException() {
		try {
			Check.ensure(true, MESSAGE);
		} catch (RuntimeException e) {
			fail("no exception should thrown.");
		}
	}

	@Test
	public void ensure_FalseConditionAndAStringPassed_ThrowsAnIllegalArgumentExceptionWithThePassedMessage() {
		try {
			Check.ensure(false, MESSAGE);
			fail("should throw a runtime exception.");
		} catch (RuntimeException e) {
			assertTrue(e instanceof IllegalArgumentException,
					"exception is not an IllegalArgumentException but a " + e.getClass().getSimpleName());
			assertEquals(MESSAGE, e.getMessage());
		}
	}

}