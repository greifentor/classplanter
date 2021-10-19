package de.ollie.classplanter.yaml;

import java.util.List;

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

	private List<String> excludeByClassName;
	private Boolean ignoreOrphans;
	private PackageMode packageMode;
	private Boolean showMembers;
	private Boolean uniteEqualAssociations;

}