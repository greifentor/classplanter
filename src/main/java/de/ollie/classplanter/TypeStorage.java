package de.ollie.classplanter;

import static de.ollie.utils.Check.ensure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.ollie.classplanter.model.TypeData;
import de.ollie.classplanter.model.TypeData.Type;

public class TypeStorage {

	private final Map<String, TypeData> storedTypeData = new HashMap<>();

	/**
	 * Type data will be added if not contained the storage. They will be identified
	 * by there qualified name.
	 * 
	 * Type data which has the type REFERENCED will be updated by type data with any
	 * other type except REFERENCED or UNKNOWN.
	 * 
	 * @param typeData The type data to add.
	 * @throws IllegalArgumentException passing a null value as typeData.
	 */
	public void addOrUpdate(TypeData typeData) {
		ensure(typeData != null, "type data cannot be null!");
		TypeData typeDataStored = storedTypeData.get(typeData.getQualifiedName());
		if ((typeDataStored != null)
				&& !((typeDataStored.getType() == Type.REFERENCED) || (typeDataStored.getType() == Type.UNKNOWN))) {
			return;
		}
		storedTypeData.put(typeData.getQualifiedName(), typeData);
	}

	public List<TypeData> getTypes() {
		return storedTypeData.entrySet().stream().map(Entry::getValue).toList();
	}

}
