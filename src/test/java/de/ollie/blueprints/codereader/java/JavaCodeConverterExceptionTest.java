package de.ollie.blueprints.codereader.java;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class JavaCodeConverterExceptionTest {

	private static final RuntimeException CAUSE = new RuntimeException();
	private static final String MESSAGE = "message";

	private JavaCodeConverterException unitUnderTest = new JavaCodeConverterException(MESSAGE, CAUSE);

	@Test
	public void constructorSetCauseCorrectly() {
		assertThat(this.unitUnderTest.getCause(), equalTo(CAUSE));
	}

	@Test
	public void constructorSetMessageCorrectly() {
		assertThat(this.unitUnderTest.getMessage(), equalTo(MESSAGE));
	}

	@Test
	public void constructorSetMessageCorrectlyForSimpleConstructorWithoutCause() {
		this.unitUnderTest = new JavaCodeConverterException(MESSAGE);
		assertThat(this.unitUnderTest.getMessage(), equalTo(MESSAGE));
	}

}