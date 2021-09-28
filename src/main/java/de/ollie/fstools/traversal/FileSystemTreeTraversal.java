package de.ollie.fstools.traversal;

import static de.ollie.utils.Check.ensure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A class which performs a tree traversal on a file system tree.
 *
 * @author ollie (01.01.2020)
 */
public class FileSystemTreeTraversal {

	private List<DirectoryFoundListener> directoryFoundListeners = new ArrayList<>();
	private List<FileFoundListener> fileFoundListeners = new ArrayList<>();
	private Path workingPath = null;

	/**
	 * Creates a new tree traversal for the passed path.
	 *
	 * @param path The path which the file system tree traversal should work with.
	 */
	public FileSystemTreeTraversal(Path workingPath) {
		super();
		ensure(workingPath != null, "path cannot be null.");
		this.workingPath = workingPath;
	}

	/**
	 * Adds the passed directory found listener to the listeners which are observing the traversal.
	 * 
	 * @param listener The new listener to add.
	 * @throws IllegalArgumentException Passing a null value.
	 */
	public void addDirectoryFoundListener(DirectoryFoundListener listener) {
		ensure(listener != null, "listener cannot be null.");
		this.directoryFoundListeners.add(listener);
	}

	/**
	 * Adds the passed file found listener to the listeners which are observing the traversal.
	 * 
	 * @param listener The new listener to add.
	 */
	public void addFileFoundListener(FileFoundListener listener) {
		ensure(listener != null, "listener cannot be null.");
		this.fileFoundListeners.add(listener);
	}

	protected void fireDirectoryFoundEvent(File file) {
		this.directoryFoundListeners.forEach(listener -> {
			try {
				listener.directoryFound(new DirectoryFoundEvent().setPath(Paths.get(file.getAbsolutePath())));
			} catch (Exception e) {
				throw new RuntimeException("error occured while calling directory found listener: " + e.getMessage() // NOSONAR
						+ ", exception type: " + e.getClass().getName());
			}
		});
	}

	protected void fireFileFoundEvent(File file) {
		this.fileFoundListeners.forEach(listener -> {
			try {
				listener.fileFound(new FileFoundEvent().setPath(Paths.get(file.getAbsolutePath())));
			} catch (Exception e) {
				throw new RuntimeException("error occured while calling file found listener: " + e.getMessage() // NOSONAR
						+ ", exception type: " + e.getClass().getName());
			}
		});
	}

	/**
	 * Returns the working path of the traversal.
	 * 
	 * @return The working path of the traversal.
	 */
	public Path getWorkingPath() {
		return this.workingPath;
	}

	/**
	 * Traverses the working tree and fires events in case detecting a file or a directory.
	 * 
	 * @throws FileNotFoundException In case of working tree is not existing.
	 */
	public void traverse() throws IOException {
		traverse(this.workingPath.toFile());
	}

	private void traverse(File file) throws IOException {
		if (!Files.isSymbolicLink(Paths.get(file.getAbsolutePath()))) {
			ensure(file.exists(), new FileNotFoundException("file does not exists: " + file.getAbsolutePath()));
			if (file.isFile()) {
				fireFileFoundEvent(file);
			} else if (file.isDirectory()) {
				fireDirectoryFoundEvent(file);
				for (File f : file.listFiles()) {
					traverse(f);
				}
			}
		}
	}

}