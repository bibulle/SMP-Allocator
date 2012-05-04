package com.em.allocator.item;

import org.bukkit.entity.Item;

public class ItemAllocatableFromDropped extends ItemAllocatable {

	private Item theItem;
	
	public ItemAllocatableFromDropped(Item item) {
		super(item.getItemStack());
		this.theItem = item;
	}


	@Override
	public void remove() {
		if (getTheItemStack().getAmount() == 1) {
			theItem.remove();
		} else {
			getTheItemStack().setAmount(getTheItemStack().getAmount() - 1);
		}
		
	}

}
