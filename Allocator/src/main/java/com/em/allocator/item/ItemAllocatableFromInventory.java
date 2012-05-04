package com.em.allocator.item;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ItemAllocatableFromInventory extends ItemAllocatable {

	private InventoryHolder inputContainer;

	public ItemAllocatableFromInventory(ItemStack itemStack, InventoryHolder inputContainer) {
		super(itemStack);
		this.inputContainer = inputContainer;
	}

	@Override
	public void remove() {
		if (getTheItemStack().getAmount() == 1) {
			inputContainer.getInventory().remove(getTheItemStack());
		} else {
			getTheItemStack().setAmount(getTheItemStack().getAmount() - 1);
		}
	}
}
