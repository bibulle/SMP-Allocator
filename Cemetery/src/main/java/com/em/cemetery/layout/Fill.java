package com.em.cemetery.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Fill extends Layout {

	private static HashMap<String, Fill> fillType = new HashMap<String, Fill>();

	static {
		// init
		new Fill_01(0, 0);
		new Fill_02(0, 0);
		new Fill_03(0, 0);
	}
	
	
	public abstract int getSignHeight();

	public Fill(int number, int slab, int pilar) {
		key = number + "_" + slab + "_" + pilar;
	}

	private String key = "";

	public String getKey() {
		return key;
	}

	protected static void addFillType(Fill fill) {
		fillType.put(fill.getKey(), fill);
	}

	public static Fill getFill(String key) {
		if (fillType.get(key) != null) {
			return fillType.get(key);
		}
		return new Fill_01(0, 0);
	}

	public static Fill getFillRandom() {
		if (fillType.isEmpty()) {
			return new Fill_01(0, 0);
		}

		List<Fill> lst = new ArrayList<Fill>(fillType.values());
		int random = (int) Math.floor(Math.random() * lst.size());
		return lst.get(random);
	}

}
