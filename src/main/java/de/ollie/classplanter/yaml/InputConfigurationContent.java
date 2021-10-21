package de.ollie.classplanter.yaml;

import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * @author ollie (10.10.2021)
 */
@Accessors(chain = true)
@Data
@Generated
public class InputConfigurationContent {

	private List<String> explicitClasses;
	private List<String> explicitPackages;

}