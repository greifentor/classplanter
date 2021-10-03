package de.ollie.classplanter;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ollie (03.10.2021)
 */
@Accessors(chain = true)
@Data
public class OutputConfiguration {

	public enum PackageMode {
		FLAT,
		NONE;
	}

	private PackageMode packageMode;

}