package de.ollie.classplanter.yaml;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ollie (10.10.2021)
 */
@Accessors(chain = true)
@Data
public class OutputConfigurationContent {

	public enum PackageMode {
		FLAT,
		NONE;
	}

	private PackageMode packageMode;
	private Boolean uniteEqualAssociations;

}