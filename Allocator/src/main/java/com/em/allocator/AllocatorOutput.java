package com.em.allocator;

import java.util.ArrayList;
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

import com.em.allocator.item.ItemAllocatable;
import com.em.chesttrap.MyInventoryModifiedEvent;

public class AllocatorOutput {

	/**
	 * Just output items as dropped item
	 * 
	 * @param inputItems
	 * @param world
	 * @param outputLocation
	 */
	public static void outputItemToDropped(List<ItemAllocatable> inputItems, World world, Location outputLocation) {

		// Smoke
		world.playEffect(outputLocation, Effect.SMOKE, 0);

		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (ItemAllocatable itemAllocatable : inputItems) {

			// try to stack or add
			boolean stacked = false;
			for (ItemStack is : stacks) {
				// if there is a not full stack add it
				if (is.getType().equals(itemAllocatable.getType()) && (is.getAmount() < is.getMaxStackSize())) {
					int newSize = is.getAmount() + 1;
					is.setAmount(newSize);
					if (newSize == is.getAmount()) {
						// Bukkit.getLogger().info(" Item dropped " + is);
						itemAllocatable.remove();
					} else {
						// Bukkit.getLogger().info(" Item not dropped " + is);
					}
					stacked = true;
					break;
				}
			}
			// not existing stack... create a new
			if (!stacked) {
				ItemStack item = itemAllocatable.getTheItemStack().clone();
				item.setAmount(1);

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

	/**
	 * Just add Item to inventory
	 * 
	 * @param inputItems
	 * @param outputContainer
	 * @param thePlugin 
	 */
	public static void outputItemToContainer(List<ItemAllocatable> inputItems, InventoryHolder outputContainer, InventoryHolder inputContainer, Allocator thePlugin) {

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

				int firstEmpty = outputContainer.getInventory().firstEmpty();
				if (firstEmpty >= 0) {
					outputContainer.getInventory().setItem(firstEmpty, item);
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
		
		final MyInventoryModifiedEvent newInputEvent = new MyInventoryModifiedEvent(inputContainer.getInventory());
		final MyInventoryModifiedEvent newOutputEvent = new MyInventoryModifiedEvent(outputContainer.getInventory());
		thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(thePlugin, new Runnable() {
			public void run() {
				Bukkit.getServer().getPluginManager().callEvent(newInputEvent);
				Bukkit.getServer().getPluginManager().callEvent(newOutputEvent);
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

		final MyInventoryModifiedEvent newInputEvent = new MyInventoryModifiedEvent(inputContainer.getInventory());
		final MyInventoryModifiedEvent newOutputEvent = new MyInventoryModifiedEvent(outputContainer.getInventory());
		thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(thePlugin, new Runnable() {
			public void run() {
				Bukkit.getServer().getPluginManager().callEvent(newInputEvent);
				Bukkit.getServer().getPluginManager().callEvent(newOutputEvent);
			}
		}, 1L);
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
