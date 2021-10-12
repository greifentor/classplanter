package de.ollie.classplanter;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
import de.ollie.fstools.traversal.FileFoundEvent;
import de.ollie.fstools.traversal.FileFoundListener;
import lombok.RequiredArgsConstructor;

/**
 * @author ollie (01.10.2021)
 */
@RequiredArgsConstructor
public class ClassPlanterFileFoundListener implements FileFoundListener {

	private static ClassTypeChecker classTypeChecker = new ClassTypeChecker();
	private static StereotypeReader stereotypeReader = new StereotypeReader();

	private static final List<String> MANY_TYPE_NAMES = List.of("List<", "Set<", "Stack<");

	private final Configuration configuration;

	private List<TypeData> types = new ArrayList<>();
	private List<AssociationData> associations = new ArrayList<>();

	@Override
	public void fileFound(FileFoundEvent event) {
		if (!event.getPath().toString().toLowerCase().endsWith(".java")) {
			return;
		}
		if (!isExplicitIncludedClass(event.getPath().toString())) {
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
												configuration)));
	}

	private boolean isExplicitIncludedClass(String fileName) {
		if ((configuration.getExplicitClasses() == null) || configuration.getExplicitClasses().isEmpty()) {
			return true;
		}
		final String className = fileName.replace("\\", "/").replace("/", ".").replace(".java", "");
		return configuration.getExplicitClasses().stream().anyMatch(className::endsWith);
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
				if (classTypeChecker.isClassType(fieldDeclaration)) {
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