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
//		System.out.println("================== InventoryCloseEvent");
		onInventoryEvent(new MyInventoryModifiedEvent(event.getInventory()));
	}
	
	/**
	 * On Inventory close
	 **/
	@EventHandler
	public void onInventoryEvent(MyInventoryModifiedEvent event) {

		InventoryHolder ih = event.getInventory().getHolder();
		if (ih instanceof Chest) {
			Chest chest = (Chest) ih;

			Block b = chest.getBlock();

			// if it's a ChestTrap
			if (!this.thePlugin.chestMap.containsKey(b.getLocation())) {
				return;
			}

			if (this.thePlugin.chestMap.get(b.getLocation()).changeInventory(event.getInventory())) {
				//System.out.println("================== MyInventoryModifiedEvent");

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
					BlockRedstoneEvent newEvent = new BlockRedstoneEvent(block, 0, 15);
					Bukkit.getServer().getPluginManager().callEvent(newEvent);
					this.thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(this.thePlugin, new Runnable() {
						public void run() {
							BlockRedstoneEvent newEvent = new BlockRedstoneEvent(blockf, 15, 0);
							Bukkit.getServer().getPluginManager().callEvent(newEvent);
						}
					}, 6L);

					// if it's a REDSTONE_WIRE, set power
					if (block.getType() == Material.REDSTONE_WIRE) {

						// I find no other way... change it to torch
						block.setType(Material.REDSTONE_TORCH_ON);
						this.thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(this.thePlugin, new Runnable() {
							public void run() {
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
