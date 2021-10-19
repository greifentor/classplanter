package de.ollie.classplanter.yaml;

import static de.ollie.utils.Check.ensure;

import java.util.function.Consumer;

import de.ollie.classplanter.Configuration;
import de.ollie.classplanter.Configuration.PackageMode;

/**
 * @author ollie (09.10.2021)
 */
public class YAMLConfigurationAdder {

	public Configuration add(Configuration configuration, YAMLConfigurationContent yamlConfigurationContent) {
		ensure(configuration != null, "configuration cannot be null.");
		ensure(yamlConfigurationContent != null, "YAML configuration content cannot be null.");
		setIfInYAMLConfigurationContent(
				yamlConfigurationContent.getInput().getExplicitClasses(),
				configuration::setExplicitClasses);
		setIfInYAMLConfigurationContent(
				yamlConfigurationContent.getInput().getExplicitPackages(),
				configuration::setExplicitPackages);
		setIfInYAMLConfigurationContent(
				yamlConfigurationContent.getOutput().getIgnoreOrphans(),
				configuration::setIgnoreOrphans);
		setIfInYAMLConfigurationContent(
				yamlConfigurationContent.getOutput().getPackageMode(),
				value -> configuration.setPackageMode(PackageMode.valueOf(value.name())));
		setIfInYAMLConfigurationContent(
				yamlConfigurationContent.getOutput().getShowMembers(),
				configuration::setShowMembers);
		setIfInYAMLConfigurationContent(
				yamlConfigurationContent.getOutput().getUniteEqualAssociations(),
				configuration::setUniteEqualAssociations);
		return configuration;
	}

	private <T> void setIfInYAMLConfigurationContent(T yamlValue, Consumer<T> consumer) {
		if (yamlValue != null) {
			consumer.accept(yamlValue);
		}
	}

}