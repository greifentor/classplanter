package de.ollie.classplanter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.ollie.classplanter.Configuration.PackageMode;
import de.ollie.classplanter.yaml.YAMLConfigurationAdder;
import de.ollie.classplanter.yaml.YAMLConfigurationContentFromYamlFileReader;
import de.ollie.fstools.traversal.FileFoundListener;
import de.ollie.fstools.traversal.FileSystemTreeTraversal;

public class ClassPlanter {

	public static StringListSplitter stringListSplitter = new StringListSplitter();

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("cnf", true, "name of the configuration file.");
		options.addOption("sf", true, "folder whose files are to read.");
		options.addOption("tf", true, "name of the target file.");
		try {
			CommandLine cmd = new DefaultParser().parse(options, args);
			List<String> sourceFolderNames = getSourceFolderNames(cmd);
			Path targetFilePath = getTargetFilePath(cmd);
			Configuration configuration = getConfigurationFromYAMLFile(getConfigurationFromProperties(), cmd);
			ClassPlanterFileFoundListener fileFoundListener = new ClassPlanterFileFoundListener(configuration);
			for (String sourceFolderName : sourceFolderNames) {
				processSourceFolder(sourceFolderName, fileFoundListener);
			}
			String result = new PlantUMLClassDiagramCreator().create(fileFoundListener, configuration);
			deleteTargetFileIfExisting(targetFilePath);
			Files.writeString(targetFilePath, result, StandardOpenOption.CREATE_NEW);
			System.out.println("wrote result to: " + targetFilePath.toString());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (ParseException pe) {
			System.out.println(pe.getMessage());
		}
	}

	private static List<String> getSourceFolderNames(CommandLine cmd) {
		List<String> sourceFolderNames = null;
		if (cmd.hasOption("sf")) {
			sourceFolderNames = stringListSplitter.split(cmd.getOptionValue("sf"));
		}
		return sourceFolderNames;
	}

	private static Path getTargetFilePath(CommandLine cmd) {
		Path targetFilePath = Path.of("result.plantuml");
		if (cmd.hasOption("tf")) {
			targetFilePath = Path.of(cmd.getOptionValue("tf"));
		}
		return targetFilePath;
	}

	private static Configuration getConfigurationFromProperties() {
		return new Configuration()
				.setExcludeByClassName(readExcludeByClassNameFromProperties())
				.setExplicitClasses(readExplicitClassNamesFromProperties())
				.setExplicitPackages(readExplicitPackageNamesFromProperties())
				.setIgnoreOrphans(Boolean.getBoolean("classplanter.output.ignoreOrphans"))
				.setPackageMode(PackageMode.valueOf(System.getProperty("classplanter.output.packageMode", "NONE")))
				.setShowMembers(Boolean.getBoolean("classplanter.output.showMembers"))
				.setUniteEqualAssociations(Boolean.getBoolean("classplanter.output.uniteEqualAssociations"));
	}

	private static List<String> readExcludeByClassNameFromProperties() {
		String classNames = System.getProperty("classplanter.output.excludeByClassName");
		return stringListSplitter.split(classNames);
	}

	private static List<String> readExplicitPackageNamesFromProperties() {
		String explicitPackageNames = System.getProperty("classplanter.input.includePackages");
		return stringListSplitter.split(explicitPackageNames);
	}

	private static List<String> readExplicitClassNamesFromProperties() {
		String explicitClassNames = System.getProperty("classplanter.input.explicitClassNames");
		return stringListSplitter.split(explicitClassNames);
	}

	private static Configuration getConfigurationFromYAMLFile(Configuration configuration, CommandLine cmd)
			throws JsonParseException, JsonMappingException, IOException {
		if (cmd.hasOption("cnf")) {
			String configurationFileName = cmd.getOptionValue("cnf");
			configuration = new YAMLConfigurationAdder()
					.add(configuration, new YAMLConfigurationContentFromYamlFileReader().read(configurationFileName));
		}
		return configuration;
	}

	private static void processSourceFolder(String sourceFolderName, FileFoundListener fileFoundListener)
			throws IOException {
		FileSystemTreeTraversal traversal = new FileSystemTreeTraversal(Path.of(sourceFolderName));
		traversal.addFileFoundListener(fileFoundListener);
		traversal.traverse();
	}

	private static void deleteTargetFileIfExisting(Path targetFilePath) throws IOException {
		if (Files.exists(targetFilePath)) {
			Files.delete(targetFilePath);
		}
	}

}