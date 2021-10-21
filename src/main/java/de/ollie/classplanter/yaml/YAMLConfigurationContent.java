package de.ollie.classplanter.yaml;

import lombok.Data;
import lombok.Generated;
import lombok.experimental.Accessors;

/**
 * @author ollie (09.10.2021)
 */
@Accessors(chain = true)
@Data
@Generated
public class YAMLConfigurationContent {

	private InputConfigurationContent input = new InputConfigurationContent();
	private OutputConfigurationContent output = new OutputConfigurationContent();

}