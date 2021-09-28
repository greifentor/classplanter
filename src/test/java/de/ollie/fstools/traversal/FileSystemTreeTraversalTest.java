package de.ollie.fstools.traversal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.fstools.Counter;

/**
 * Unit tests for class "FileSystemTreeTraversal".
 *
 * @author ollie (01.01.2020)
 */
@ExtendWith(MockitoExtension.class)
public class FileSystemTreeTraversalTest {

	private static final Path PATH = Paths.get("/");

	private FileSystemTreeTraversal unitUnderTest;

	@DisplayName("Tests of method 'constructor(Path)'.")
	@Nested
	class TestsOfMethod_constructor_Path {

		@DisplayName("Throws an exception passing a null value as path.")
		@Test
		void passANullValueAsPath_ThrowsAnException() {
			assertThrows(IllegalArgumentException.class, () -> new FileSystemTreeTraversal(null));
		}

		@DisplayName("Sets the passed path as working path for the traversal.")
		@Test
		void passAPathObject_SetsThePassedPathAsWorkingPathForTheTraversal() {
			// run
			unitUnderTest = new FileSystemTreeTraversal(PATH);
			// check
			assertThat(unitUnderTest.getWorkingPath(), equalTo(PATH));
		}

	}

	@DisplayName("Tests of method 'addDirectoryFoundListener(DirectoryFoundListener)'.")
	@Nested
	class TestsOfMethod_addDirectoryFoundListener_DirectoryFoundListener {

		@DisplayName("Throws an exception if the listener is passed as null.")
		@Test
		void passANullValue_ThrowsAnException() {
			assertThrows(IllegalArgumentException.class,
					() -> new FileSystemTreeTraversal(PATH).addDirectoryFoundListener(null));
		}

	}

	@DisplayName("Tests of method 'addFileFoundListener(FileFoundListener)'.")
	@Nested
	class TestsOfMethod_addFileFoundListener_FileFoundListener {

		@DisplayName("Throws an exception if the listener is passed as null.")
		@Test
		void passANullValue_ThrowsAnException() {
			assertThrows(IllegalArgumentException.class,
					() -> new FileSystemTreeTraversal(PATH).addFileFoundListener(null));
		}

	}

	@DisplayName("Tests of method 'traverse()'.")
	@Nested
	class TestsOfMethod_traverse {

		@DisplayName("Throws an exception if the working path is not existing.")
		@Test
		void workingPathIsNotExisting_ThrowsAnException() {
			assertThrows(FileNotFoundException.class,
					() -> new FileSystemTreeTraversal(Paths.get("/NotExisting")).traverse());
		}

		@DisplayName("Throws an exception if some thing goes wrong while calling the file found listener.")
		@Test
		void somethingGoesWrongWhileCallingTheFileFoundListener_ThrowsAnException() throws Exception {
			// Prepare
			RuntimeException exception = new RuntimeException();
			unitUnderTest = new FileSystemTreeTraversal(Paths.get("src", "test", "resources", "testfolder"));
			unitUnderTest.addFileFoundListener(event -> {
				throw exception;
			});
			// Run & Check
			assertThrows(RuntimeException.class, () -> unitUnderTest.traverse());
		}

		@DisplayName("Fires a file found event for any file found.")
		@Test
		void workingPathIsExisting_FiresAFileFoundEventForAnyFileFound() throws Exception {
			// Prepare
			Counter count = new Counter();
			unitUnderTest = new FileSystemTreeTraversal(Paths.get("src", "test", "resources", "testfolder"));
			unitUnderTest.addFileFoundListener(event -> count.inc());
			// Run
			unitUnderTest.traverse();
			// Check
			assertThat(count.getCount(), equalTo(3));
		}

		@DisplayName("Throws an exception if some thing goes wrong while calling the directory found listener.")
		@Test
		void somethingGoesWrongWhileCallingTheDirectoryFoundListener_ThrowsAnException() throws Exception {
			// Prepare
			RuntimeException exception = new RuntimeException();
			unitUnderTest = new FileSystemTreeTraversal(Paths.get("src", "test", "resources", "testfolder"));
			unitUnderTest.addDirectoryFoundListener(event -> {
				throw exception;
			});
			// Run & Check
			assertThrows(RuntimeException.class, () -> unitUnderTest.traverse());
		}

		@DisplayName("Fires a directory found event for any directory found.")
		@Test
		void workingPathIsExisting_FiresADirectoryFoundEventForAnyDirectoryFound() throws Exception {
			// Prepare
			Counter count = new Counter();
			unitUnderTest = new FileSystemTreeTraversal(Paths.get("src", "test", "resources", "testfolder"));
			unitUnderTest.addDirectoryFoundListener(event -> count.inc());
			// Run
			unitUnderTest.traverse();
			// Check
			assertThat(count.getCount(), equalTo(3));
		}

	}

}