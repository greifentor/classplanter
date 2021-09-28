package de.ollie.fstools.traversal;

/**
 * An interface for file found listeners.
 *
 * @author ollie (02.01.2020)
 */
@FunctionalInterface
public interface FileFoundListener {

	/**
	 * Is called when the file system tree traversal finds a file.
	 * 
	 * @param event The event data for the found file.
	 */
	void fileFound(FileFoundEvent event);

}