package de.ollie.classplanter;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.ollie.blueprints.codereader.java.JavaCodeConverter;
import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.CompilationUnit;
import de.ollie.blueprints.codereader.java.model.EnumDeclaration;
import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.blueprints.codereader.java.model.ImportDeclaration;
import de.ollie.blueprints.codereader.java.model.InterfaceDeclaration;
import de.ollie.blueprints.codereader.java.model.Modifier;
import de.ollie.blueprints.codereader.java.model.TypeDeclaration;
import de.ollie.classplanter.Configuration.PackageMode;
import de.ollie.classplanter.model.AssociationData;
import de.ollie.classplanter.model.AssociationData.AssociationType;
import de.ollie.classplanter.model.ClassKeyData;
import de.ollie.classplanter.model.MemberData;
import de.ollie.classplanter.model.MemberData.Visibility;
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
	private static PackageAgent packageAgent = new PackageAgent();
	private static StereotypeReader stereotypeReader = new StereotypeReader();
	private static ManyTypeChecker manyTypeChecker = new ManyTypeChecker();
	private static SuperInterfaceAgent superInterfaceAgent = new SuperInterfaceAgent();

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
		compilationUnit.getTypeDeclarations()
				.stream()
				.filter(this::isToImport)
				.forEach(typeDeclaration -> compilationUnitMembers.add(new TypeData()
						.addSuperInterfaceNames(superInterfaceAgent.getSuperInterfaceNames(typeDeclaration))
						.setClassName(typeDeclaration.getName())
						.setMembers(getMembers(typeDeclaration))
						.setPackageName(compilationUnit.getPackageName())
						.setStereotypes(stereotypeReader.getStereotypes(typeDeclaration))
						.setSuperClassName(getSuperClassName(typeDeclaration))
						.setType(getType(typeDeclaration))));
		types.addAll(compilationUnitMembers);
		types = cleanUpTypes(types);
		compilationUnit.getTypeDeclarations()
				.stream()
				.filter(this::isToImportAsAssociation)
				.forEach(typeDeclaration -> associations.addAll(getAssociationsOfTypeDeclaration(typeDeclaration,
						compilationUnit.getPackageName(),
						compilationUnit.getImportDeclarations(),
						compilationUnitMembers,
						configuration)));
	}

	private boolean isToImport(TypeDeclaration typeDeclaration) {
		return !configuration.isClassToExclude(typeDeclaration.getName());
	}

	private boolean isToImportAsAssociation(TypeDeclaration typeDeclaration) {
		return !configuration.isClassToExclude(typeDeclaration.getName());
	}

	private List<MemberData> getMembers(TypeDeclaration typeDeclaration) {
		return typeDeclaration instanceof ClassDeclaration
				? ((ClassDeclaration) typeDeclaration).getFields()
						.stream()
						.map(fieldDeclaration -> new MemberData().setName(fieldDeclaration.getName())
								.setType(fieldDeclaration.getType())
								.setVisibility(getVisibility(fieldDeclaration))
								.setModifiers(getModifiers(fieldDeclaration)))
						.collect(Collectors.toList())
				: typeDeclaration instanceof EnumDeclaration
						? ((EnumDeclaration) typeDeclaration).getIdentifiers()
								.stream()
								.map(identifier -> new MemberData().setName(identifier))
								.collect(Collectors.toList())
						: new ArrayList<>();
	}

	private Visibility getVisibility(FieldDeclaration fieldDeclaration) {
		for (Modifier modifier : fieldDeclaration.getModifiers()) {
			if (modifier == Modifier.PRIVATE) {
				return Visibility.PRIVATE;
			} else if (modifier == Modifier.PROTECTED) {
				return Visibility.PROTECTED;
			} else if (modifier == Modifier.PUBLIC) {
				return Visibility.PUBLIC;
			}
		}
		return Visibility.PACKAGE_PRIVATE;
	}

	private Set<MemberData.Modifier> getModifiers(FieldDeclaration fieldDeclaration) {
		return fieldDeclaration.getModifiers()
				.stream()
				.filter(modifier -> getModifier(modifier) != null)
				.map(modifier -> getModifier(modifier))
				.collect(Collectors.toSet());
	}

	private MemberData.Modifier getModifier(Modifier modifier) {
		if (modifier == Modifier.FINAL) {
			return MemberData.Modifier.FINAL;
		} else if (modifier == Modifier.STATIC) {
			return MemberData.Modifier.STATIC;
		}
		return null;
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
		} else if (typeDeclaration instanceof EnumDeclaration) {
			return Type.ENUM;
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
		return types.stream()
				.filter(typeData -> (typeData.getType() == Type.CLASS) || (typeData.getType() == Type.ABSTRACT_CLASS)
						|| (typeData.getType() == Type.ENUM) || (typeData.getType() == Type.INTERFACE)
						|| (typeData.getType() == Type.REFERENCED))
				.collect(Collectors.toList());
	}

	public List<AssociationData> getAssociations() {
		return associations;
	}

	private List<AssociationData> getAssociationsOfTypeDeclaration(TypeDeclaration typeDeclaration,
			String typePackageName, List<ImportDeclaration> importDeclarations, List<TypeData> compilationUnitMembers,
			Configuration configuration) {
		Collection<AssociationData> associations = configuration.isUniteEqualAssociations()
				? new HashSet<>()
				: new ArrayList<>();
		if (typeDeclaration instanceof ClassDeclaration) {
			for (FieldDeclaration fieldDeclaration : ((ClassDeclaration) typeDeclaration).getFields()) {
				if (configuration.isClassToExclude(manyTypeChecker.removeManyType(fieldDeclaration.getType()))) {
					continue;
				}
				if (isAClassType(fieldDeclaration.getType(), configuration)) {
					AssociationData associationData = new AssociationData()
							.setFieldName(configuration.isUniteEqualAssociations() ? null : fieldDeclaration.getName())
							.setFrom(new ClassKeyData().setClassName(typeDeclaration.getName())
									.setPackageName(typePackageName))
							.setTo(new ClassKeyData()
									.setClassName(manyTypeChecker.removeManyType(fieldDeclaration.getType()))
									.setPackageName(getPackageName(fieldDeclaration.getType(),
											compilationUnitMembers,
											typePackageName,
											importDeclarations)))
							.setType(getAssociationType(fieldDeclaration.getType()));
					associations.add(associationData);
					if ((configuration.getPackageMode() == PackageMode.FLAT)
							&& (associationData.getTo().getPackageName() != null)) {
						TypeData typeData = new TypeData().setClassName(associationData.getTo().getClassName())
								.setPackageName(associationData.getTo().getPackageName())
								.setStereotypes(stereotypeReader.getStereotypes(typeDeclaration))
								.setType(Type.REFERENCED);
						if (!isTypeAlreadyKnown(typeData)) {
							types.add(typeData);
						}
					}
				}
			}
		}
		return new ArrayList<>(associations);
	}

	private boolean isAClassType(String typeName, Configuration configuration) {
		if (isEnumAndShouldBeHandledAsSimpleClass(typeName, configuration)) {
			return false;
		}
		return classTypeChecker.isClassType(typeName);
	}

	private boolean isEnumAndShouldBeHandledAsSimpleClass(String typeName, Configuration configuration) {
		return configuration.isHandleEnumsAsSimpleTypes() && isEnumType(typeName);
	}

	private boolean isEnumType(String typeName) {
		return types.stream()
				.anyMatch(typeData -> (typeData.getType() == Type.ENUM) && (typeData.getClassName().equals(typeName)));
	}

	private boolean isTypeAlreadyKnown(TypeData typeData) {
		return types.stream()
				.anyMatch(typeDataStored -> Objects.equals(typeDataStored.getClassName(), typeData.getClassName())
						&& Objects.equals(typeDataStored.getPackageName(), typeData.getPackageName()));
	}

	private String getPackageName(String typeName, List<TypeData> compilationUnitMembers,
			String compilationUnitPackageName, List<ImportDeclaration> importDeclarations) {
		return packageAgent
				.findPackageNameForType(typeName,
						compilationUnitMembers,
						compilationUnitPackageName,
						importDeclarations)
				.orElse(compilationUnitPackageName);
	}

	private AssociationType getAssociationType(String type) {
		return manyTypeChecker.isManyType(type) ? AssociationType.MANY_TO_ONE : AssociationType.ONE_TO_ONE;
	}

	private List<TypeData> cleanUpTypes(List<TypeData> typeData) {
		List<TypeData> typesToRemove = new ArrayList<>();
		for (TypeData type : typeData) {
			List<TypeData> sameOnes = findAllByQualifiedName(typeData, type.getQualifiedName());
			if (sameOnes.size() > 1) {
				sameOnes.stream()
						.filter(td -> (td.getType() == Type.REFERENCED) || (td.getType() == Type.UNKNOWN))
						.forEach(typesToRemove::add);
			}
		}
		typesToRemove.forEach(typeData::remove);
		return typeData;
	}

	private List<TypeData> findAllByQualifiedName(List<TypeData> typeData, String qualifiedName) {
		return typeData.stream().filter(td -> td.getQualifiedName().equals(qualifiedName)).collect(Collectors.toList());
	}

}