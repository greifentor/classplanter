package de.ollie.fstools.traversal;

/**
 * An interface for directory found listeners.
 *
 * @author ollie (02.01.2020)
 */
@FunctionalInterface
public interface DirectoryFoundListener {

	/**
	 * Is called when the file system tree traversal finds a directory.
	 * 
	 * @param event The event data for the found directory.
	 */
	void directoryFound(DirectoryFoundEvent event);

}