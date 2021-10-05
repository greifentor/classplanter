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
import java.util.stream.Stream;

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
import de.ollie.classplanter.OutputConfiguration.PackageMode;
import de.ollie.classplanter.model.AssociationData;
import de.ollie.classplanter.model.AssociationData.AssociationType;
import de.ollie.classplanter.model.ClassKeyData;
import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;
import de.ollie.fstools.traversal.FileFoundEvent;
import de.ollie.fstools.traversal.FileFoundListener;
import de.ollie.fstools.traversal.FileSystemTreeTraversal;
import lombok.RequiredArgsConstructor;

public class ClassPlanter {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("sf", true, "folder whose files are to read.");
		options.addOption("tf", true, "name of the target file.");
		try {
			CommandLine cmd = new DefaultParser().parse(options, args);
			if (cmd.hasOption("sf")) {
				OutputConfiguration outputConfiguration = new OutputConfiguration()
						.setExplicitPackages(readExplicitPackageNamesFromProperties())
						.setPackageMode(
								PackageMode.valueOf(System.getProperty("classplanter.output.packagemode", "NONE")))
						.setUniteEqualAssociations(Boolean.getBoolean("classplanter.output.unite.equal.associations"));
				String folderName = cmd.getOptionValue("sf");
				FileSystemTreeTraversal traversal = new FileSystemTreeTraversal(Path.of(folderName));
				ClassPlanterFileFoundListener fileFoundListener =
						new ClassPlanterFileFoundListener(outputConfiguration);
				traversal.addFileFoundListener(fileFoundListener);
				traversal.traverse();
				String result = new PlantUMLClassDiagramCreator().create(fileFoundListener, outputConfiguration);
				Path targetFilePath = Path.of("result.plantuml");
				if (cmd.hasOption("tf")) {
					targetFilePath = Path.of(cmd.getOptionValue("tf"));
				}
				if (Files.exists(targetFilePath)) {
					Files.delete(targetFilePath);
				}
				Files.writeString(targetFilePath, result, StandardOpenOption.CREATE_NEW);
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (ParseException pe) {
			System.out.println(pe.getMessage());
		}
	}

	private static List<String> readExplicitPackageNamesFromProperties() {
		String explicitPackageNames = System.getProperty("classplanter.input.include.packages");
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

	private final OutputConfiguration outputConfiguration;

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
								|| (typeData.getType() == Type.INTERFACE))
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
			OutputConfiguration outputConfiguration) {
		Collection<AssociationData> associations =
				outputConfiguration.isUniteEqualAssociations() ? new HashSet<>() : new ArrayList<>();
		if (typeDeclaration instanceof ClassDeclaration) {
			for (FieldDeclaration fieldDeclaration : ((ClassDeclaration) typeDeclaration).getFields()) {
				if (isClassType(fieldDeclaration)) {
					associations
							.add(
									new AssociationData()
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
											.setType(getAssociationType(fieldDeclaration.getType())));
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

	public String create(ClassPlanterFileFoundListener fileFoundListener, OutputConfiguration outputConfiguration) {
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

	private String getClassCode(List<TypeData> classes, OutputConfiguration outputConfiguration) {
		Stream<TypeData> filteredClasses =
				classes.stream().filter(typeData -> isExplicitPackage(typeData.getPackageName(), outputConfiguration));
		if (outputConfiguration.getPackageMode() == PackageMode.FLAT) {
			List<TypeData> types = filteredClasses
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
				code += "\t" + getTypeKeyWord(typeData) + typeData.getClassName() + getSuperClassExtension(typeData)
						+ getSuperInterfaceImplementations(typeData) + " {\n\t}\n\n";
			}
			return code + "}\n";
		}
		return filteredClasses
				.sorted(
						(typeData0,
								typeData1) -> typeData0.getClassName().compareToIgnoreCase(typeData1.getClassName()))
				.map(
						typeData -> getTypeKeyWord(typeData) + typeData.getClassName()
								+ getSuperClassExtension(typeData) + getSuperInterfaceImplementations(typeData)
								+ " {\n}\n")
				.reduce((s0, s1) -> s0 + "\n" + s1)
				.orElse("");
	}

	private boolean isExplicitPackage(String typePackageName, OutputConfiguration outputConfiguration) {
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

	private String getAssociationCode(List<AssociationData> associations, OutputConfiguration outputConfiguration) {
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