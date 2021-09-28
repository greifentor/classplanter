package de.ollie.fstools.traversal;

import java.nio.file.Path;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * A container for file found events.
 *
 * @author ollie (02.01.2020)
 */
@Accessors(chain = true)
@Data
@Generated
public class FileFoundEvent {

	private Path path;

}