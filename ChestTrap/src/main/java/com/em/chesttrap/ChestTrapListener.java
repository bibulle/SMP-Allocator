package com.em.chesttrap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.RedstoneWire;

public class ChestTrapListener implements Listener {

	ChestTrap thePlugin;

	/**
	 * Constructor
	 **/
	public ChestTrapListener(ChestTrap plugin) {
		this.thePlugin = plugin;
	}

	/**
	 * On destruction of Block
	 **/
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// if it's a ChestTrap
		if (this.thePlugin.chestMap.containsKey(event.getBlock().getLocation())) {
			// Remove it from the list
			this.thePlugin.chestMap.remove(event.getBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.GREEN + "Chest removed!");
		}
	}

	// /**
	// * On redstone change of a block
	// **/
	// @EventHandler
	// public void onInventoryEvent(FurnaceSmeltEvent event) {
	//
	// System.out.println(event.getEventName() + " " + event.getBlock());
	// }

	/**
	 * On Inventory close
	 **/
	@EventHandler
	public void onInventoryEvent(InventoryCloseEvent event) {
		// System.out.println("================== InventoryCloseEvent");
		onInventoryEvent(new MyInventoryModifiedEvent(event.getInventory()));
	}

	/**
	 * On Inventory close
	 **/
	@EventHandler
	public void onInventoryEvent(MyInventoryModifiedEvent event) {
		// System.out.println("================== MyInventoryModifiedEvent 1");

		if (event.getInventory() == null) {
			return;
		}

		//System.out.println("================== MyInventoryModifiedEvent 2");
		InventoryHolder ih = event.getInventory().getHolder();
		//System.out.println("================== MyInventoryModifiedEvent 2 " + ih);

		if (ih instanceof DoubleChest) {
			DoubleChest doubleChest = (DoubleChest) ih;
			eventReceivedOnChest(event, doubleChest.getRightSide());
			eventReceivedOnChest(event, doubleChest.getLeftSide());
		} else {
			eventReceivedOnChest(event, ih);
		}
	}

	/**
	 * We just managed the event on a Chest (twice if it's a double chest
	 * @param event the event
	 * @param ih the single chast
	 */
	private void eventReceivedOnChest(MyInventoryModifiedEvent event, InventoryHolder ih) {
		Chest chest = null;
		if (ih instanceof Chest) {
			chest = (Chest) ih;
		}
		if (chest != null) {
			Block b = chest.getBlock();
			
			boolean changed = this.thePlugin.chestMap.containsKey(b.getLocation()) && this.thePlugin.chestMap.get(b.getLocation()).changeInventory(event.getInventory());
			//System.out.println("================== MyInventoryModifiedEvent 4");
			if (changed) {
				//System.out.println("================== MyInventoryModifiedEvent 5");

				// Inventory change, set on power !!!!
				List<Block> array = new ArrayList<Block>();
				array.add(b.getRelative(BlockFace.NORTH));
				array.add(b.getRelative(BlockFace.SOUTH));
				array.add(b.getRelative(BlockFace.EAST));
				array.add(b.getRelative(BlockFace.WEST));
				array.add(b.getRelative(BlockFace.UP));
				array.add(b.getRelative(BlockFace.DOWN));

				for (Block block : array) {
					final Block blockf = block;

					// Send an event (for others mod)
					//BlockRedstoneEvent newEvent = new BlockRedstoneEvent(block, 0, 15);
					//Bukkit.getServer().getPluginManager().callEvent(newEvent);
					this.thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(this.thePlugin, new Runnable() {
						public void run() {
							BlockRedstoneEvent newEvent = new BlockRedstoneEvent(blockf, 0, 15);
							Bukkit.getServer().getPluginManager().callEvent(newEvent);
						}
					}, 5L);
					this.thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(this.thePlugin, new Runnable() {
						public void run() {
							BlockRedstoneEvent newEvent = new BlockRedstoneEvent(blockf, 15, 0);
							Bukkit.getServer().getPluginManager().callEvent(newEvent);
						}
					}, 8L);

					// if it's a REDSTONE_WIRE, set power
					if (block.getType() == Material.REDSTONE_WIRE) {

						// I find no other way... change it to torch
						block.setTypeId(0);
						block.setType(Material.REDSTONE_TORCH_ON);
						this.thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(this.thePlugin, new Runnable() {
							public void run() {
								blockf.setTypeId(0);
								blockf.setType(Material.REDSTONE_WIRE);
							}
						}, 6L);

					}
				}
			}
		}
	}

	/**
	 * On inventory open
	 **/
	// @EventHandler
	// public void onInventoryEvent(InventoryOpenEvent event) {
	//
	// System.out.println(event.getEventName() + " " + event.getInventory());
	// }
	/**
	 * On redstone change of a block
	 **/
	// @EventHandler
	// public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
	//
	// System.out.println(event.getEventName()+" "+event.getBlock());
	// }

}
