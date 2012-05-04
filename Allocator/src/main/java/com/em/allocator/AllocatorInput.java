package com.em.allocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.em.allocator.item.ItemAllocatable;
import com.em.allocator.item.ItemAllocatableFromDropped;
import com.em.allocator.item.ItemAllocatableFromInventory;

public class AllocatorInput {

	
	/**
	 * Returns a random list of item from the dropped items
	 * @param thePlugin 
	 */
	public static List<ItemAllocatable> getRandomItemFromDropped(World world, Location inputLocation, Material filter, Allocator thePlugin) {
		List<ItemAllocatable> items = new ArrayList<ItemAllocatable>();

		Arrow a = world.spawnArrow(inputLocation, new Vector(0, 0, 0), 0.0F, 0.0F);
		List<Entity> entities = a.getNearbyEntities(0.5D, 0.5D, 0.5D);
		a.remove();

		for (int l = 0; l < entities.size(); l++) {
			if (entities.get(l) instanceof Item) {
				Item item = (Item) entities.get(l);
				// if item not dead add to the entities list
				if (!item.isDead()) {
					items.add(new ItemAllocatableFromDropped(item));
				}
			}
		}
		return limitItemList(items, thePlugin, filter);
	}

	/**
	 * Returns a random list of item from the container
	 * @param thePlugin 
	 */
	public static List<ItemAllocatable> getRandomItemFromContainer(InventoryHolder inventory, Random rand, Block block, Material filter, Allocator thePlugin) {
		List<ItemAllocatable> items = new ArrayList<ItemAllocatable>();

		if (inventory == null) {
			return items;
		}
		
		int startAt = 0;

		// First create a list that is going to be reorder randomly
		List<ItemStack> itemsTemp = new ArrayList<ItemStack>();

		// Only use the last slot, if it is a furnace,
		// because it doesn't make sense to take something out of the first slots as
		// they are inputs
		if (inventory instanceof Furnace) {
			startAt = 2;
		}
		for (int k = startAt; k < inventory.getInventory().getSize(); k++) {
			if (inventory.getInventory().getItem(k) != null) {
				itemsTemp.add(inventory.getInventory().getItem(k));
			}
		}

		// Just sort it randomly
		while (!itemsTemp.isEmpty()) {
			int r = (int) Math.floor(Math.random() * itemsTemp.size());
			items.add(new ItemAllocatableFromInventory(itemsTemp.get(r), inventory));
			itemsTemp.remove(r);
		}

		return limitItemList(items, thePlugin, filter);
	}

	/**
	 * limit the movable item list
	 * 
	 * @param itemAllocables
	 * @param inputContainer
	 * @param thePlugin
	 * @param filter
	 * @param dropped
	 * @return
	 */
	private static List<ItemAllocatable> limitItemList(List<ItemAllocatable> itemAllocables, Allocator thePlugin, Material filter) {
		List<ItemAllocatable> items = new ArrayList<ItemAllocatable>();

		// Temporary stack (to count items)
		List<ItemStack> stacks = new ArrayList<ItemStack>();

		// for each potential stack
		for (ItemAllocatable itemAllocable : itemAllocables) {

			// first... must be filtered ?
			if (passesFilter(filter, itemAllocable.getTheItemStack(), thePlugin)) {

				// for each item in the Stack
				for (int i = 0; i < itemAllocable.getAmount(); i++) {
					if (!thePlugin.quantityIsStack) {
						// we limit by Item numbers
						if (items.size() < thePlugin.quantityDropped) {
							items.add(itemAllocable);
						}
					} else {
						// we limit by stack
						// try to stack or add
						boolean stacked = false;
						for (ItemStack is : stacks) {
							// if there is a not full stack add it
							if (is.getType().equals(itemAllocable.getType()) && (is.getAmount() < is.getMaxStackSize())) {
								is.setAmount(is.getAmount() + 1);

								items.add(itemAllocable);
								stacked = true;
								break;
							}
						}
						// not existing stack... create a new
						if (!stacked && stacks.size() < thePlugin.quantityDropped) {
							stacks.add(new ItemStack(itemAllocable.getType(), 1));
							items.add(itemAllocable);
						}
					}

				}

			}

		}

		return items;

	}

	/**
	 * Returns true, if the item is allowed to pass
	 * 
	 * @param filter
	 * @param itemStack
	 * @param thePlugin
	 * @return
	 */
	static private boolean passesFilter(Material filter, ItemStack itemStack, Allocator thePlugin) {
		if (!thePlugin.allowFiltering) {
			return true;
		}

		// No filter
		if (filter.equals(Material.AIR)) {
			return true;
		}

		return itemStack.getType().equals(filter);
	}


}
