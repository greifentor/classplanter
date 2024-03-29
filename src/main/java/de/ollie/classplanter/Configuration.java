package de.ollie.classplanter;

import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * @author ollie (03.10.2021)
 */
@Accessors(chain = true)
@Data
@Generated
public class Configuration {

	public enum PackageMode {
		FLAT,
		NONE;
	}

	private List<String> excludeByClassName;
	private List<String> explicitClasses;
	private List<String> explicitPackages;
	private boolean handleEnumsAsSimpleTypes;
	private boolean ignoreConstants;
	private boolean ignoreOrphans;
	private PackageMode packageMode;
	private boolean showMembers;
	private boolean uniteEqualAssociations;

}