package de.ollie.classplanter;

import java.util.List;

import de.ollie.classplanter.model.MemberData;
import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MembersToPlantUMLConverter {

	private final ClassTypeChecker classTypeChecker = new ClassTypeChecker();
	private final VisibilityToPlantUMLConverter visibilityToPlantUMLConverter = new VisibilityToPlantUMLConverter();

	public String createMemberCode(TypeData typeData, Configuration configuration, boolean indent,
			List<TypeData> types) {
		return configuration.isShowMembers() ? getMembersCode(typeData, indent, configuration, types) : "";
	}

	private String getMembersCode(TypeData typeData, boolean indent, Configuration configuration,
			List<TypeData> types) {
		return typeData.getMembers()
				.stream()
				.filter(member -> !classTypeChecker.isAClassType(member.getType(), configuration, types))
				.filter(member -> !configuration.isIgnoreConstants() || !member.isConstant())
				.sorted(this::compareMembers)
				.map(member -> "\t" + (indent ? "\t" : "") + getMemberCode(typeData, member, configuration))
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

	private String getMemberCode(TypeData typeData, MemberData member, Configuration configuration) {
		if (typeData.getType() == Type.ENUM) {
			return member.getName();
		}
		return visibilityToPlantUMLConverter.getPlantUMLString(member.getVisibility()) + " "
				+ (member.isStatic() ? "{static} " : "")
				+ (member.isFinal() && !configuration.isSuppressFinal() ? "final " : "") + member.getName() + " : "
				+ member.getType();
	}

}