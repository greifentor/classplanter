package de.ollie.classplanter.yaml;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author ollie (09.10.2021)
 */
public class YAMLConfigurationContentFromYamlFileReader {

	public YAMLConfigurationContent read(String fileName) throws JsonParseException, JsonMappingException, IOException {
		if (fileName == null) {
			return null;
		}
		File file = new File(fileName);
		ObjectMapper om = new ObjectMapper(new YAMLFactory());
		return om.readValue(file, YAMLConfigurationContent.class);
	}

}