package com.em.chesttrap;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemChestTrapItem {

	// list of item in the inventory
	private HashMap<Material, Integer> inventoryContent = new HashMap<Material, Integer>();

	public HashMap<Material, Integer> getInventoryContent() {
		return inventoryContent;
	}

	public void setInventoryContent(HashMap<Material, Integer> inventoryContent) {
		this.inventoryContent = inventoryContent;
	}

	public ItemChestTrapItem(Inventory inventory) {

		for (ItemStack itemStack : inventory) {
			Material material = itemStack.getType();
			int amount = itemStack.getAmount();

			if (inventoryContent.get(material) != null) {
				amount += inventoryContent.get(material);
			}

			inventoryContent.put(material, amount);

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
		
		ItemChestTrapItem newChest = new ItemChestTrapItem(inventory);
		
		for (Material material : newChest.getInventoryContent().keySet()) {
			if (newChest.getAmount(material) != this.getAmount(material)) {
				return true;
			}
		}
		for (Material material : this.getInventoryContent().keySet()) {
			if (newChest.getAmount(material) != this.getAmount(material)) {
				return true;
			}
		}

		return false;
	}

}
