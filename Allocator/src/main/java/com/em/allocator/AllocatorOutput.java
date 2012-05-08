package com.em.allocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebeaninternal.server.subclass.GetterSetterMethods;
import com.em.allocator.item.ItemAllocatable;
import com.em.chesttrap.MyInventoryModifiedEvent;

public class AllocatorOutput {

	/**
	 * Just output items as dropped item
	 * 
	 * @param inputItems
	 * @param world
	 * @param outputLocation
	 * @param thePlugin
	 */
	public static void outputItemToDropped(List<ItemAllocatable> inputItems, World world, Location outputLocation, Allocator thePlugin) {

		// System.out.println("+++ " + inputItems);
		// for (Iterator iterator = inputItems.iterator(); iterator.hasNext();) {
		// ItemAllocatable itemAllocatable = (ItemAllocatable) iterator.next();
		// System.out.println("+++    " +
		// itemAllocatable.getType()+" "+itemAllocatable.getAmount());
		// }


		// the counter to limit dropped
		List<ItemStack> stackDropped = new ArrayList<ItemStack>();

		boolean isOneItemAllocated = false;

		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (ItemAllocatable itemAllocatable : inputItems) {
			// for each item in the Stack
			int itemAllocatableAmount = itemAllocatable.getAmount();
			for (int i = 0; i < itemAllocatableAmount; i++) {
				// System.out.println("=== " + itemAllocatable);
				// try to stack or add
				boolean stacked = false;
				for (ItemStack is : stacks) {
					// if there is a not full stack add it
					if (is.getType().equals(itemAllocatable.getType()) && (is.getAmount() < is.getMaxStackSize())) {
						// limit to count (via config)
						boolean canBeDropped = limitDropCount(is, stackDropped, thePlugin);
						if (canBeDropped) {

							// really added it to the target (and remove it from the input)
							isOneItemAllocated = true;
							int newSize = is.getAmount() + 1;
							is.setAmount(newSize);
							if (newSize == is.getAmount()) {
								// Bukkit.getLogger().info(" Item dropped " + is);
								itemAllocatable.remove();
							} else {
								// Bukkit.getLogger().info(" Item not dropped " + is);
							}
						}
						stacked = true;
						break;
					}
				}
				// not existing stack... create a new
				if (!stacked) {
					ItemStack item = itemAllocatable.getTheItemStack().clone();
					item.setAmount(1);

					// limit to count (via config)
					boolean canBeDropped = limitDropCount(item, stackDropped, thePlugin);
					if (canBeDropped) {

						// really added it to the target (and remove it from the input)
						isOneItemAllocated = true;
						Item droppedItem = world.dropItem(outputLocation, item);
						if (droppedItem != null) {
							stacks.add(droppedItem.getItemStack());
							// Bukkit.getLogger().info(" Item dropped " + item);
							itemAllocatable.remove();
						} else {
							// Bukkit.getLogger().info(" Item not dropped " + item);
						}
					}
				}
			}
		}
		
		if (isOneItemAllocated) {
			// Smoke
			world.playEffect(outputLocation, Effect.SMOKE, 0);
		}

	}

	/**
	 * Just add Item to inventory
	 * 
	 * @param inputItems
	 * @param outputContainer
	 * @param thePlugin
	 */
	public static void outputItemToContainer(List<ItemAllocatable> inputItems, InventoryHolder outputContainer, InventoryHolder inputContainer, Allocator thePlugin) {

		// the counter to limit dropped
		List<ItemStack> stackDropped = new ArrayList<ItemStack>();

		boolean isOneItemAllocated = false;

		for (ItemAllocatable itemAllocatable : inputItems) {
			// for each item in the Stack
			int itemAllocatableAmount = itemAllocatable.getAmount();
			for (int j = 0; j < itemAllocatableAmount; j++) {

				// try to stack or add
				boolean stacked = false;
				ItemStack[] stacks = outputContainer.getInventory().getContents();
				for (int i = 0; i < stacks.length; i++) {
					ItemStack is = stacks[i];
					if (is == null) {
						continue;
					}
					// if there is a not full stack add it
					if (is.getType().equals(itemAllocatable.getType()) && (is.getAmount() < is.getMaxStackSize())) {

						// limit to count (via config)
						boolean canBeDropped = limitDropCount(is, stackDropped, thePlugin);
						if (canBeDropped) {

							// really added it to the target (and remove it from the input)
							isOneItemAllocated = true;
							int newSize = is.getAmount() + 1;
							is.setAmount(newSize);
							if (newSize == is.getAmount()) {
								// Bukkit.getLogger().info(" Item added " + is);
								itemAllocatable.remove();
							} else {
								// Bukkit.getLogger().info(" Item not added " + is);
							}
						}
						stacked = true;
						break;
					}
				}
				// not existing stack... create a new
				if (!stacked) {
					ItemStack item = itemAllocatable.getTheItemStack().clone();
					item.setAmount(1);

					int firstEmpty = outputContainer.getInventory().firstEmpty();
					if (firstEmpty >= 0) {
						// limit to count (via config)
						boolean canBeDropped = limitDropCount(item, stackDropped, thePlugin);
						if (canBeDropped) {

							// really added it to the target (and remove it from the input)
							isOneItemAllocated = true;
							outputContainer.getInventory().setItem(firstEmpty, item);
							if (outputContainer.getInventory().contains(item)) {
								// Bukkit.getLogger().info(" Item added " + item);
								itemAllocatable.remove();
							} else {
								// Bukkit.getLogger().info(" Item not added " + item);
							}
						}
					} else {
						// Bukkit.getLogger().info(" Item not added " + item);
					}
				}
			}
		}

		if (isOneItemAllocated) {
			sendInventoryEvent(outputContainer, inputContainer, thePlugin);
		}
	}

	/**
	 * Can this item be dropped (du to count limits in config)
	 * 
	 * @param is
	 * @param stackDropped
	 * @param thePlugin
	 * @return
	 */
	private static boolean limitDropCount(ItemStack is, List<ItemStack> stackDropped, Allocator thePlugin) {
		boolean dropped = false;
		// Count sent items
		if (!thePlugin.quantityIsStack) {
			// System.out.println("--- Items " + stackDropped.size());
			// In case of item filter, just count down
			if (stackDropped.size() < thePlugin.quantityDropped) {
				stackDropped.add(is);
				dropped = true;
			} else {
				dropped = false;
			}
			// System.out.println("--- Items " + stackDropped.size() + "->" +
			// dropped);
		} else {
			// System.out.println("--- Stack " + stackDropped.size());
			// In case of stack filter, try to add it in stackDropped count
			for (ItemStack droppedItemStack : stackDropped) {
				// try to add it into the dropped stack
				// System.out.println(droppedItemStack.getAmount()+" "+is.getMaxStackSize());
				if (is.getType().equals(droppedItemStack.getType()) && (droppedItemStack.getAmount() < is.getMaxStackSize())) {
					droppedItemStack.setAmount(droppedItemStack.getAmount() + 1);
					dropped = true;
				}
			}
			// Not added try to create a new stack
			if (!dropped && (stackDropped.size() < thePlugin.quantityDropped)) {
				stackDropped.add(new ItemStack(is.getType(), 1));
				dropped = true;
			}
			// System.out.println("--- Stack " + stackDropped.size() + "->" +
			// dropped);

		}
		return dropped;
	}

	private static void sendInventoryEvent(InventoryHolder outputContainer, InventoryHolder inputContainer, Allocator thePlugin) {
		final InventoryHolder inputContainerf = inputContainer;
		final InventoryHolder outputContainerf = outputContainer;
		thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(thePlugin, new Runnable() {
			public void run() {
				if (inputContainerf != null) {
					Bukkit.getServer().getPluginManager().callEvent(new MyInventoryModifiedEvent(inputContainerf.getInventory()));
				}
				if (outputContainerf != null) {
					Bukkit.getServer().getPluginManager().callEvent(new MyInventoryModifiedEvent(outputContainerf.getInventory()));
				}
			}
		}, 1L);
	}

	/**
	 * Just add Item to inventory
	 * 
	 * @param inputItems
	 * @param outputContainer
	 */
	public static void outputItemToFurnace(List<ItemAllocatable> inputItems, Furnace outputContainer, InventoryHolder inputContainer, Allocator thePlugin) {

		for (ItemAllocatable itemAllocatable : inputItems) {

			// try to stack or add
			boolean stacked = false;
			ItemStack[] stacks = outputContainer.getInventory().getContents();
			for (int i = 0; i < stacks.length; i++) {
				ItemStack is = stacks[i];
				if (is == null) {
					continue;
				}
				// if there is a not full stack add it
				if (is.getType().equals(itemAllocatable.getType()) && (is.getAmount() < is.getMaxStackSize())) {
					int newSize = is.getAmount() + 1;
					is.setAmount(newSize);
					if (newSize == is.getAmount()) {
						// Bukkit.getLogger().info(" Item added " + is);
						itemAllocatable.remove();
					} else {
						// Bukkit.getLogger().info(" Item not added " + is);
					}
					stacked = true;
					break;
				}
			}
			// not existing stack... create a new
			if (!stacked) {
				ItemStack item = itemAllocatable.getTheItemStack().clone();
				item.setAmount(1);

				// if it's fuel
				if (isFuel(itemAllocatable.getTheItemStack()) && (outputContainer.getInventory().getFuel() == null)) {
					outputContainer.getInventory().setFuel(item);
					if (outputContainer.getInventory().contains(item)) {
						// Bukkit.getLogger().info(" Item added " + item);
						itemAllocatable.remove();
					} else {
						// Bukkit.getLogger().info(" Item not added " + item);
					}
				} else if (outputContainer.getInventory().getSmelting() == null) {
					outputContainer.getInventory().setSmelting(item);
					if (outputContainer.getInventory().contains(item)) {
						// Bukkit.getLogger().info(" Item added " + item);
						itemAllocatable.remove();
					} else {
						// Bukkit.getLogger().info(" Item not added " + item);
					}
				} else {
					// Bukkit.getLogger().info(" Item not added " + item);
				}
			}
		}

		sendInventoryEvent(outputContainer, inputContainer, thePlugin);
	}

	/**
	 * Method inspired from net.minecraft.server.TileEntityFurnace
	 */
	public static boolean isFuel(ItemStack itemstack) {
		return burning.contains(itemstack.getType());
	}

	static List<Material> burning = new ArrayList<Material>();
	static {
		burning.add(Material.WOOD);
		burning.add(Material.STICK);
		burning.add(Material.COAL);
		burning.add(Material.LAVA_BUCKET);
		burning.add(Material.SAPLING);
		burning.add(Material.BLAZE_ROD);
	}

}
