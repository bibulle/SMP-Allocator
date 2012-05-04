package com.em.chesttrap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
		// if it's a Chest
		if (this.thePlugin.chestMap.containsKey(event.getBlock().getLocation())) {
			// Remove it from the list
			this.thePlugin.chestMap.remove(event.getBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.GREEN + "Chest removed!");
		}
	}

	/**
	 * On redstone change of a block
	 **/
	@EventHandler
	public void onInventoryEvent(FurnaceSmeltEvent event) {
		
		System.out.println(event.getEventName()+" "+event.getBlock());
	}
	/**
	 * On redstone change of a block
	 **/
	@EventHandler
	public void onInventoryEvent(InventoryClickEvent event) {
		
		System.out.println(event.getEventName()+" "+event.getInventory().getHolder());
	}
	/**
	 * On redstone change of a block
	 **/
	@EventHandler
	public void onInventoryEvent(InventoryCloseEvent event) {
		
		InventoryHolder ih = event.getInventory().getHolder();
		if (ih instanceof Chest) {
			Chest chest = (Chest)ih;
			
			Block b = chest.getBlock();
			
			List<Block> array = new ArrayList<Block>();
			array.add(b.getRelative(BlockFace.NORTH));
			array.add(b.getRelative(BlockFace.SOUTH));
			array.add(b.getRelative(BlockFace.EAST));
			array.add(b.getRelative(BlockFace.WEST));
			array.add(b.getRelative(BlockFace.UP));
			array.add(b.getRelative(BlockFace.DOWN));
			
			for (Block block : array) {
				BlockRedstoneEvent newEvent = new BlockRedstoneEvent(block, 0, 15);
				Bukkit.getServer().getPluginManager().callEvent(newEvent);
			}
			 
			
		// Call the event
			 
			 // Now you do the event
			 //Bukkit.getServer().broadcastMessage(event.getMessage());
			 
			System.out.println(event.getEventName()+" "+chest.getBlock());
		}
	}
	/**
	 * On redstone change of a block
	 **/
	@EventHandler
	public void onInventoryEvent(InventoryOpenEvent event) {
		
		System.out.println(event.getEventName()+" "+event.getInventory());
	}
	/**
	 * On redstone change of a block
	 **/
//	@EventHandler
//	public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
//		
//		System.out.println(event.getEventName()+" "+event.getBlock());
//	}


}
