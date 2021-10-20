package de.ollie.classplanter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.ollie.classplanter.Configuration.PackageMode;
import de.ollie.classplanter.model.AssociationData;
import de.ollie.classplanter.model.AssociationData.AssociationType;
import de.ollie.classplanter.model.ClassKeyData;
import de.ollie.classplanter.model.MemberData;
import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;

/**
 * @author ollie (01.10.2021)
 */
public class PlantUMLClassDiagramCreator {

	private static ClassTypeChecker classTypeChecker = new ClassTypeChecker();
	private static VisibilityToPlantUMLConverter visibilityToPlantUMLConverter = new VisibilityToPlantUMLConverter();

	public String create(ClassPlanterFileFoundListener fileFoundListener, Configuration outputConfiguration) {
		String code = "@startuml\n" //
				+ "\n" //
				+ "{0}" //
				+ "\n" //
				+ "{1}" //
				+ "@enduml" //
		;
		code = code
				.replace(
						"{0}",
						getClassCode(
								fileFoundListener.getClasses(),
								fileFoundListener.getAssociations(),
								outputConfiguration));
		code = code.replace("{1}", getAssociationCode(fileFoundListener.getAssociations(), outputConfiguration));
		return code;
	}

	private String getClassCode(List<TypeData> classes, List<AssociationData> associations,
			Configuration configuration) {
		List<TypeData> filteredClasses = classes
				.stream()
				.filter(
						typeData -> isExplicitPackage(typeData.getPackageName(), configuration)
								&& !isExcludedOrphan(typeData, associations, configuration))
				.collect(Collectors.toList());
		if (configuration.getPackageMode() == PackageMode.FLAT) {
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
				code += "\t" + createClassHeader(typeData) + " {\n" + createMemberCode(typeData, configuration, true)
						+ "\t}\n\n";
			}
			return code + "}\n";
		}
		return filteredClasses
				.stream()
				.sorted(
						(typeData0,
								typeData1) -> typeData0.getClassName().compareToIgnoreCase(typeData1.getClassName()))
				.map(
						typeData -> createClassHeader(typeData) + " {\n"
								+ createMemberCode(typeData, configuration, false) + "}\n")
				.reduce((s0, s1) -> s0 + "\n" + s1)
				.orElse("");
	}

	private boolean isExcludedOrphan(TypeData typeData, List<AssociationData> associations,
			Configuration configuration) {
        return !configuration.isIgnoreOrphans() ? false : isAnOrphan(typeData, associations, configuration);
	}

    private boolean isAnOrphan(TypeData typeData, List<AssociationData> associations, Configuration configuration) {
		return !associations
				.stream()
				.anyMatch(
                        association -> (matches(association.getFrom(), typeData)
                                || matches(association.getTo(), typeData))
                                && (isExplicitPackage(association.getFrom().getPackageName(), configuration)
                                        && isExplicitPackage(association.getTo().getPackageName(), configuration)));
	}

    private boolean matches(ClassKeyData classKey, TypeData typeData) {
		return Objects.equals(classKey.getClassName(), typeData.getClassName())
                && Objects.equals(classKey.getPackageName(), typeData.getPackageName());
	}

	private String createClassHeader(TypeData typeData) {
		return getTypeKeyWord(typeData) + typeData.getClassName()
				+ getSuperClassExtension(typeData)
				+ getSuperInterfaceImplementations(typeData)
				+ getStereotypes(typeData);
	}

	private String createMemberCode(TypeData typeData, Configuration outputConfiguration, boolean indent) {
		return outputConfiguration.isShowMembers() ? getMembersCode(typeData, indent) : "";
	}

	private String getMembersCode(TypeData typeData, boolean indent) {
		return typeData
				.getMembers()
				.stream()
				.filter(member -> !classTypeChecker.isClassType(member.getType()))
				.sorted(this::compareMembers)
				.map(member -> "\t" + (indent ? "\t" : "") + getMemberCode(typeData, member))
				.reduce((s0, s1) -> s0 + "\n" + s1)
				.map(s -> s + "\n")
				.orElse("");
	}

	private int compareMembers(MemberData m0, MemberData m1) {
		int result = m0.getVisibility().ordinal() - m1.getVisibility().ordinal();
		if (result == 0) {
			result = m0.getName().compareTo(m1.getName());
		}
		return result;
	}

	private String getMemberCode(TypeData typeData, MemberData member) {
		if (typeData.getType() == Type.ENUM) {
			return member.getName();
		}
		return visibilityToPlantUMLConverter.getPlantUMLString(member.getVisibility()) + " " + member.getName() + " : "
				+ member.getType();
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
		if (typeData.getType() == Type.ENUM) {
			return "enum ";
		} else if (typeData.getType() == Type.INTERFACE) {
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
                                outputConfiguration)
                                && isExplicitPackage(associationData.getTo().getPackageName(), outputConfiguration))
				.map(
						associationData -> getAssociation(associationData)
                                + (outputConfiguration.isShowMembers()
                                        && !outputConfiguration.isUniteEqualAssociations()
                                                ? " : " + associationData.getFieldName()
                                                : "")
								+ "\n")
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