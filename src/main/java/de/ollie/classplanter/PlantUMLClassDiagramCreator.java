package de.ollie.classplanter;

import java.util.List;
import java.util.stream.Collectors;

import de.ollie.classplanter.Configuration.PackageMode;
import de.ollie.classplanter.model.AssociationData;
import de.ollie.classplanter.model.AssociationData.AssociationType;
import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;

/**
 * @author ollie (01.10.2021)
 */
public class PlantUMLClassDiagramCreator {

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