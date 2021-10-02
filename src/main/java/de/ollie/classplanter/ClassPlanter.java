package de.ollie.classplanter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.ollie.blueprints.codereader.java.JavaCodeConverter;
import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.CompilationUnit;
import de.ollie.blueprints.codereader.java.model.InterfaceDeclaration;
import de.ollie.blueprints.codereader.java.model.Modifier;
import de.ollie.blueprints.codereader.java.model.TypeDeclaration;
import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;
import de.ollie.fstools.traversal.FileFoundEvent;
import de.ollie.fstools.traversal.FileFoundListener;
import de.ollie.fstools.traversal.FileSystemTreeTraversal;

public class ClassPlanter {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("sf", true, "folder whose files are to read.");
		options.addOption("tf", true, "name of the target file.");
		try {
			CommandLine cmd = new DefaultParser().parse(options, args);
			if (cmd.hasOption("sf")) {
				String folderName = cmd.getOptionValue("sf");
				FileSystemTreeTraversal traversal = new FileSystemTreeTraversal(Path.of(folderName));
				ClassPlanterFileFoundListener fileFoundListener = new ClassPlanterFileFoundListener();
				traversal.addFileFoundListener(fileFoundListener);
				traversal.traverse();
				String result = new PlantUMLClassDiagramCreator().create(fileFoundListener);
				if (cmd.hasOption("tf")) {
					Path targetFilePath = Path.of(cmd.getOptionValue("tf"));
					if (Files.exists(targetFilePath)) {
						Files.delete(targetFilePath);
					}
					Files.writeString(targetFilePath, result, StandardOpenOption.CREATE_NEW);
				}
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (ParseException pe) {
			System.out.println(pe.getMessage());
		}
	}

}

class ClassPlanterFileFoundListener implements FileFoundListener {

	private List<TypeData> types = new ArrayList<>();

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
		compilationUnit
				.getTypeDeclarations()
				.forEach(
						typeDeclaration -> types
								.add(
										new TypeData()
												.addSuperInterfaceNames(getSuperInterfaceNames(typeDeclaration))
												.setClassName(typeDeclaration.getName())
												.setPackageName(compilationUnit.getPackageName())
												.setSuperClassName(getSuperClassName(typeDeclaration))
												.setType(getType(typeDeclaration))));
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

	public String[] getSuperInterfaceNames(TypeDeclaration typeDeclaration) {
		if (typeDeclaration instanceof ClassDeclaration) {
			return ((ClassDeclaration) typeDeclaration).getImplementedInterfaceNames().toArray(new String[0]);
		} else if (typeDeclaration instanceof InterfaceDeclaration) {
			return ((InterfaceDeclaration) typeDeclaration).getSuperInterfaceNames().toArray(new String[0]);
		}
		return new String[0];
	}

}

class PlantUMLClassDiagramCreator {

	public String create(ClassPlanterFileFoundListener fileFoundListener) {
		String code = "@startuml\n" //
				+ "\n" //
				+ "{0}" //
				+ "\n" //
				+ "@enduml" //
		;
		code = code.replace("{0}", getClassCode(fileFoundListener.getClasses()));
		return code;
	}

	private String getClassCode(List<TypeData> classes) {
		return classes
				.stream()
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

}