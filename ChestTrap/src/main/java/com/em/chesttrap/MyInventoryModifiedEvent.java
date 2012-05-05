package com.em.chesttrap;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class MyInventoryModifiedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Inventory inventory;

	public MyInventoryModifiedEvent(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
