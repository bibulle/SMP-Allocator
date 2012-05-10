package com.em.fan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.util.Vector;

public class FanListener implements Listener {

	Fan thePlugin;

	List<Material> powerList = new ArrayList<Material>();

	// List<Location> dupePreventer = new ArrayList<Location>();

	/**
	 * Constructor
	 **/
	public FanListener(Fan plugin) {
		this.thePlugin = plugin;

		this.powerList.add(Material.REDSTONE_WIRE);
		this.powerList.add(Material.REDSTONE_TORCH_ON);
		this.powerList.add(Material.REDSTONE_TORCH_OFF);
		this.powerList.add(Material.WOOD_PLATE);
		this.powerList.add(Material.STONE_PLATE);
		this.powerList.add(Material.DETECTOR_RAIL);
	}

	/**
	 * On destruction of Block
	 **/
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// if it's a Allocator
		if (this.thePlugin.fanMap.containsKey(event.getBlock().getLocation())) {
			// Remove it from the list
			this.thePlugin.fanMap.remove(event.getBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.GREEN + "Fan removed!");
		}
	}

	/**
	 * On redstone change of a block
	 **/
	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		// if it's a Allocator
		if (this.thePlugin.fanMap.containsKey(event.getBlock().getLocation())) {

			// Just do the job
			FanBlock al = this.thePlugin.fanMap.get(event.getBlock().getLocation());
			if ((al.getPower() == 0) && (event.getNewCurrent() > 0)) {
				// If no... do the job
				al.blow();
			}

			al.setPower(event.getNewCurrent());
		}
	}


}
