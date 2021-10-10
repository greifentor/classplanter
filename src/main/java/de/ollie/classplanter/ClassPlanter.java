package de.ollie.classplanter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.ollie.blueprints.codereader.java.JavaCodeConverter;
import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.CompilationUnit;
import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.blueprints.codereader.java.model.ImportDeclaration;
import de.ollie.blueprints.codereader.java.model.InterfaceDeclaration;
import de.ollie.blueprints.codereader.java.model.Modifier;
import de.ollie.blueprints.codereader.java.model.TypeDeclaration;
import de.ollie.classplanter.Configuration.PackageMode;
import de.ollie.classplanter.model.AssociationData;
import de.ollie.classplanter.model.AssociationData.AssociationType;
import de.ollie.classplanter.model.ClassKeyData;
import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;
import de.ollie.classplanter.yaml.YAMLConfigurationAdder;
import de.ollie.classplanter.yaml.YAMLConfigurationContentFromYamlFileReader;
import de.ollie.fstools.traversal.FileFoundEvent;
import de.ollie.fstools.traversal.FileFoundListener;
import de.ollie.fstools.traversal.FileSystemTreeTraversal;
import lombok.RequiredArgsConstructor;

public class ClassPlanter {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("cnf", true, "name of the configuration file.");
		options.addOption("sf", true, "folder whose files are to read.");
		options.addOption("tf", true, "name of the target file.");
		try {
			CommandLine cmd = new DefaultParser().parse(options, args);
			String sourceFolderName = null;
			if (cmd.hasOption("sf")) {
				sourceFolderName = cmd.getOptionValue("sf");
			}
			Path targetFilePath = Path.of("result.plantuml");
			if (cmd.hasOption("tf")) {
				targetFilePath = Path.of(cmd.getOptionValue("tf"));
			}
			Configuration configuration = new Configuration()
					.setExplicitPackages(readExplicitPackageNamesFromProperties())
					.setPackageMode(PackageMode.valueOf(System.getProperty("classplanter.output.packageMode", "NONE")))
					.setUniteEqualAssociations(Boolean.getBoolean("classplanter.output.uniteEqualAssociations"));
			if (cmd.hasOption("cnf")) {
				String configurationFileName = cmd.getOptionValue("cnf");
				configuration = new YAMLConfigurationAdder()
						.add(
								configuration,
								new YAMLConfigurationContentFromYamlFileReader().read(configurationFileName));
			}
			FileSystemTreeTraversal traversal = new FileSystemTreeTraversal(Path.of(sourceFolderName));
			ClassPlanterFileFoundListener fileFoundListener = new ClassPlanterFileFoundListener(configuration);
			traversal.addFileFoundListener(fileFoundListener);
			traversal.traverse();
			String result = new PlantUMLClassDiagramCreator().create(fileFoundListener, configuration);
			if (Files.exists(targetFilePath)) {
				Files.delete(targetFilePath);
			}
			Files.writeString(targetFilePath, result, StandardOpenOption.CREATE_NEW);
			System.out.println("wrote result to: " + targetFilePath.toString());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (ParseException pe) {
			System.out.println(pe.getMessage());
		}
	}

	private static List<String> readExplicitPackageNamesFromProperties() {
		String explicitPackageNames = System.getProperty("classplanter.input.includePackages");
		if (explicitPackageNames != null) {
			StringTokenizer st = new StringTokenizer(explicitPackageNames, ",");
			List<String> explicitPackages = new ArrayList<>();
			while (st.hasMoreTokens()) {
				explicitPackages.add(st.nextToken());
			}
			return explicitPackages;
		}
		return null;
	}

}

@RequiredArgsConstructor
class ClassPlanterFileFoundListener implements FileFoundListener {

	private static StereotypeReader stereotypeReader = new StereotypeReader();

	private static final List<String> SIMPLE_TYPE_NAMES = List
			.of(
					"boolean",
					"Boolean",
					"byte",
					"Byte",
					"char",
					"Character",
					"double",
					"Double",
					"float",
					"Float",
					"int",
					"Integer",
					"long",
					"Long",
					"short",
					"Short",
					"String");
	private static final List<String> MANY_TYPE_NAMES = List.of("List<", "Set<", "Stack<");

	private final Configuration outputConfiguration;

	private List<TypeData> types = new ArrayList<>();
	private List<AssociationData> associations = new ArrayList<>();

	@Override
	public void fileFound(FileFoundEvent event) {
		if (!event.getPath().toString().toLowerCase().endsWith(".java")) {
			return;
		}
		String fileContent;
		try {
			System.out.println("processing file: " + event.getPath());
			fileContent = Files.readString(event.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		CompilationUnit compilationUnit = new JavaCodeConverter().convert(fileContent);
		List<TypeData> compilationUnitMembers = new ArrayList<>();
		compilationUnit
				.getTypeDeclarations()
				.forEach(
						typeDeclaration -> compilationUnitMembers
								.add(
										new TypeData()
												.addSuperInterfaceNames(getSuperInterfaceNames(typeDeclaration))
												.setClassName(typeDeclaration.getName())
												.setPackageName(compilationUnit.getPackageName())
												.setStereotypes(stereotypeReader.getStereotypes(typeDeclaration))
												.setSuperClassName(getSuperClassName(typeDeclaration))
												.setType(getType(typeDeclaration))));
		types.addAll(compilationUnitMembers);
		compilationUnit
				.getTypeDeclarations()
				.forEach(
						typeDeclaration -> associations
								.addAll(
										getAssociationsOfTypeDeclaration(
												typeDeclaration,
												compilationUnit.getPackageName(),
												compilationUnit.getImportDeclarations(),
												compilationUnitMembers,
												outputConfiguration)));
	}

	private Type getType(TypeDeclaration typeDeclaration) {
		if (typeDeclaration instanceof ClassDeclaration) {
			if (((ClassDeclaration) typeDeclaration).getModifiers().contains(Modifier.ABSTRACT)) {
				return Type.ABSTRACT_CLASS;
			}
			return Type.CLASS;
		} else if (typeDeclaration instanceof InterfaceDeclaration) {
			return Type.INTERFACE;
		}
		return Type.UNKNOWN;
	}

	private String getSuperClassName(TypeDeclaration typeDeclaration) {
		if (typeDeclaration instanceof ClassDeclaration) {
			return ((ClassDeclaration) typeDeclaration).getSuperClassName();
		}
		return null;
	}

	public List<TypeData> getClasses() {
		return types
				.stream()
				.filter(
						typeData -> (typeData.getType() == Type.CLASS) || (typeData.getType() == Type.ABSTRACT_CLASS)
								|| (typeData.getType() == Type.INTERFACE) || (typeData.getType() == Type.REFERENCED))
				.collect(Collectors.toList());
	}

	public List<AssociationData> getAssociations() {
		return associations;
	}

	private String[] getSuperInterfaceNames(TypeDeclaration typeDeclaration) {
		if (typeDeclaration instanceof ClassDeclaration) {
			return ((ClassDeclaration) typeDeclaration).getImplementedInterfaceNames().toArray(new String[0]);
		} else if (typeDeclaration instanceof InterfaceDeclaration) {
			return ((InterfaceDeclaration) typeDeclaration).getSuperInterfaceNames().toArray(new String[0]);
		}
		return new String[0];
	}

	private List<AssociationData> getAssociationsOfTypeDeclaration(TypeDeclaration typeDeclaration,
			String typePackageName, List<ImportDeclaration> importDeclarations, List<TypeData> compilationUnitMembers,
			Configuration outputConfiguration) {
		Collection<AssociationData> associations =
				outputConfiguration.isUniteEqualAssociations() ? new HashSet<>() : new ArrayList<>();
		if (typeDeclaration instanceof ClassDeclaration) {
			for (FieldDeclaration fieldDeclaration : ((ClassDeclaration) typeDeclaration).getFields()) {
				if (isClassType(fieldDeclaration)) {
					AssociationData associationData = new AssociationData()
							.setFrom(
									new ClassKeyData()
											.setClassName(typeDeclaration.getName())
											.setPackageName(typePackageName))
							.setTo(
									new ClassKeyData()
											.setClassName(removeManyType(fieldDeclaration.getType()))
											.setPackageName(
													getPackageName(
															fieldDeclaration.getType(),
															compilationUnitMembers,
															typePackageName,
															importDeclarations)))
							.setType(getAssociationType(fieldDeclaration.getType()));
					associations.add(associationData);
					if ((outputConfiguration.getPackageMode() == PackageMode.FLAT)
							&& (associationData.getTo().getPackageName() != null)) {
						types
								.add(
										new TypeData()
												.setClassName(associationData.getTo().getClassName())
												.setPackageName(associationData.getTo().getPackageName())
												.setStereotypes(stereotypeReader.getStereotypes(typeDeclaration))
												.setType(Type.REFERENCED));
					}
				}
			}
		}
		return new ArrayList<>(associations);
	}

	private String getPackageName(String typeName, List<TypeData> compilationUnitMembers,
			String compilationUnitPackageName, List<ImportDeclaration> importDeclarations) {
		return new PackageAgent()
				.findPackageNameForType(
						typeName,
						compilationUnitMembers,
						compilationUnitPackageName,
						importDeclarations)
				.orElse(null);
	}

	private boolean isClassType(FieldDeclaration fieldDeclaration) {
		return !SIMPLE_TYPE_NAMES.contains(fieldDeclaration.getType());
	}

	private AssociationType getAssociationType(String type) {
		return isManyType(type) ? AssociationType.MANY_TO_ONE : AssociationType.ONE_TO_ONE;
	}

	private boolean isManyType(String type) {
		for (String typePrefix : MANY_TYPE_NAMES) {
			if (type.startsWith(typePrefix)) {
				return true;
			}
		}
		return false;
	}

	private String removeManyType(String type) {
		for (String typePrefix : MANY_TYPE_NAMES) {
			if (type.startsWith(typePrefix)) {
				return type.substring(0, type.length() - 1).replace(typePrefix, "");
			}
		}
		return type.replace("<", "").replace(">", "").replace(",", "").replace("[]", "Arr");
	}

}

class PlantUMLClassDiagramCreator {

	public String create(ClassPlanterFileFoundListener fileFoundListener, Configuration outputConfiguration) {
		String code = "@startuml\n" //
				+ "\n" //
				+ "{0}" //
				+ "\n" //
				+ "{1}" //
				+ "@enduml" //
		;
		code = code.replace("{0}", getClassCode(fileFoundListener.getClasses(), outputConfiguration));
		code = code.replace("{1}", getAssociationCode(fileFoundListener.getAssociations(), outputConfiguration));
		return code;
	}

	private String getClassCode(List<TypeData> classes, Configuration outputConfiguration) {
		List<TypeData> filteredClasses = classes
				.stream()
				.filter(typeData -> isExplicitPackage(typeData.getPackageName(), outputConfiguration))
				.collect(Collectors.toList());
		if (outputConfiguration.getPackageMode() == PackageMode.FLAT) {
			List<TypeData> types = filteredClasses
					.stream()
					.sorted(
							(typeData0, typeData1) -> typeData0
									.getQualifiedName()
									.compareToIgnoreCase(typeData1.getQualifiedName()))
					.collect(Collectors.toList());
			String code = "";
			String previousPackageName = "";
			for (TypeData typeData : types) {
				if (!previousPackageName.equals(typeData.getPackageName())) {
					if (!previousPackageName.equals("")) {
						code += "}\n\n";
					}
					previousPackageName = typeData.getPackageName();
					code += "package " + typeData.getPackageName() + " {\n\n";
				}
				code += "\t" + createClassHeader(typeData) + " {\n\t}\n\n";
			}
			return code + "}\n";
		}
		return filteredClasses
				.stream()
				.sorted(
						(typeData0,
								typeData1) -> typeData0.getClassName().compareToIgnoreCase(typeData1.getClassName()))
				.map(typeData -> createClassHeader(typeData) + " {\n}\n")
				.reduce((s0, s1) -> s0 + "\n" + s1)
				.orElse("");
	}

	private String createClassHeader(TypeData typeData) {
		return getTypeKeyWord(typeData) + typeData.getClassName()
				+ getSuperClassExtension(typeData)
				+ getSuperInterfaceImplementations(typeData)
				+ getStereotypes(typeData);
	}

	private boolean isExplicitPackage(String typePackageName, Configuration outputConfiguration) {
		if (outputConfiguration.getExplicitPackages() == null) {
			return true;
		}
		return outputConfiguration
				.getExplicitPackages()
				.stream()
				.anyMatch(packageName -> packageName.equals(typePackageName));
	}

	private String getTypeKeyWord(TypeData typeData) {
		if (typeData.getType() == Type.INTERFACE) {
			return "interface ";
		}
		return (typeData.getType() == Type.ABSTRACT_CLASS ? "abstract " : "") + "class ";
	}

	private String getSuperClassExtension(TypeData typeData) {
		return typeData.getSuperClassName() != null ? " extends " + typeData.getSuperClassName() : "";
	}

	private String getSuperInterfaceImplementations(TypeData typeData) {
		String interfaceNames =
				typeData.getSuperInterfaceNames().stream().reduce((s0, s1) -> s0 + ", " + s1).orElse("");
		return !interfaceNames.isEmpty() ? " implements " + interfaceNames : "";
	}

	private String getStereotypes(TypeData typeData) {
		return typeData
				.getStereotypes()
				.stream()
				.map(s -> " << " + s + " >>")
				.reduce((s0, s1) -> s0 + ", " + s1)
				.orElse("");
	}

	private String getAssociationCode(List<AssociationData> associations, Configuration outputConfiguration) {
		return associations
				.stream()
				.filter(
						associationData -> isExplicitPackage(
								associationData.getFrom().getPackageName(),
								outputConfiguration))
				.map(associationData -> getAssociation(associationData) + "\n")
				.reduce((s0, s1) -> s0 + "\n" + s1)
				.map(s -> s + "\n")
				.orElse("");
	}

	private String getAssociation(AssociationData association) {
		return association.getFrom().getClassName() + " --> "
				+ (association.getType() == AssociationType.MANY_TO_ONE ? "\"*\" " : "")
				+ association.getTo().getClassName();
	}

}