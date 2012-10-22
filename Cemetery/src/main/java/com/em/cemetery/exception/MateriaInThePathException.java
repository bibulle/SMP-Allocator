package com.em.cemetery.exception;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Material;

public class MateriaInThePathException extends PathNotClearException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2539380841568136769L;
	
	private Set<Material> materials;
	
	public MateriaInThePathException(Set<Material> onThePathMaterials) {
		this.materials = onThePathMaterials;
	}

	public Set<Material> getOnThePathMaterials() {
		return materials;
	}
	
	@Override
	public String getMessage() {
		String msg = "";
		for (Iterator<Material> iterator = materials.iterator(); iterator.hasNext();) {
			if (msg.length() != 0) {
				msg += ", ";
			}
			msg += iterator.next();
		}

		return "there is materials in the path ("+msg+")";
	}

}
