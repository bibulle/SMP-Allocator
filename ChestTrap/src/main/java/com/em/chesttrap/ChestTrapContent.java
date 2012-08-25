package com.em.chesttrap;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestTrapContent {

	// Sort for this chesttrap
	private SortType sort = SortType.NONE;

	// list of item in the inventory
	private HashMap<Material, Integer> inventoryContent = new HashMap<Material, Integer>();

	public HashMap<Material, Integer> getInventoryContent() {
		return inventoryContent;
	}

	public void setInventoryContent(HashMap<Material, Integer> inventoryContent) {
		this.inventoryContent = inventoryContent;
	}

	public ChestTrapContent(Inventory inventory, String sortS) {

		for (ItemStack itemStack : inventory) {
			if (itemStack == null) {
				continue;
			}
			Material material = itemStack.getType();
			int amount = itemStack.getAmount();

			if (inventoryContent.get(material) != null) {
				amount += inventoryContent.get(material);
			}

			inventoryContent.put(material, amount);

		}

		try {
			sort = SortType.valueOf(sortS);
		} catch (IllegalArgumentException e) {
		}

	}

	private int getAmount(Material material) {
		if (inventoryContent.get(material) == null) {
			return 0;
		} else {
			return inventoryContent.get(material);
		}
	}

	/**
	 * Change the inventory (and return true if modified)
	 * 
	 * @param inventory
	 *          the new one
	 * @return true if modified
	 */
	public boolean changeInventory(Inventory inventory) {

		ChestTrapContent newChest = new ChestTrapContent(inventory, sort.toString());

		for (Material material : newChest.getInventoryContent().keySet()) {
			if (newChest.getAmount(material) != this.getAmount(material)) {
				// System.out.println(material+" "+this.getAmount(material)+"->"+newChest.getAmount(material));
				setInventoryContent(newChest.getInventoryContent());
				return true;
			}
		}
		for (Material material : this.getInventoryContent().keySet()) {
			if (newChest.getAmount(material) != this.getAmount(material)) {
				// System.out.println(material+" "+this.getAmount(material)+"->"+newChest.getAmount(material));
				setInventoryContent(newChest.getInventoryContent());
				return true;
			}
		}

		return false;
	}

	/**
	 * Display the content of the chest
	 * 
	 * @return
	 */
	public String contentToText() {
		String ret = "";

		for (Material material : inventoryContent.keySet()) {
			if (ret.length() != 0) {
				ret += " ";
			}
			ret += material.toString().toLowerCase() + ":" + inventoryContent.get(material);
		}

		if (ret.length() == 0) {
			ret = "empty";
		}

		return ret;
	}

	public SortType getSort() {
		return sort;
	}

	public void setSort(SortType sort) {
		this.sort = sort;
	}

	enum SortType {
		NONE, SIMPLE, LINE, COL;

		public String toString() {
			if (this == NONE) {
				return "";
			} else {
				return super.toString();
			}
		};
	}

}
