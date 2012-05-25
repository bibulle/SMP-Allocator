package com.em.profession;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

public class ProfessionListener implements Listener {

	Profession thePlugin;

	/**
	 * Constructor
	 **/
	public ProfessionListener(Profession plugin) {
		this.thePlugin = plugin;
	}

	/**
	 * On destruction of Block
	 **/
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// if it's a Allocator
		if (this.thePlugin.professionMap.containsKey(event.getBlock().getLocation())) {
			// Remove it from the list
			this.thePlugin.professionMap.remove(event.getBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.GREEN + "Profession removed!");
		}
	}

	/**
	 * On redstone change of a block
	 **/
	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		Location location = event.getBlock().getLocation();
		// if it's a Allocator
		if (this.thePlugin.professionMap.containsKey(location)) {

			// Just do the job
			ProfessionBlock fb = this.thePlugin.professionMap.get(location);
			if (event.getNewCurrent() > 0) {
				// If no... do the job
				System.out.println("looking for villager");
				for (Villager villager : getVillagersAtLocation(location)) {
					System.out.println("Villager found : "+villager.getProfession());
					if (villager.getProfession() == null) {
						villager.setProfession(fb.profession);
					}
				}
			}
		}
	}

	/**
	 * Utilities to get Villagers at a Location
	 * 
	 * @param inputLocation
	 * @param world
	 * @return
	 */
	private List<Villager> getVillagersAtLocation(Location inputLocation) {

		List<Villager> entities = new ArrayList<Villager>();
		Chunk chunk = inputLocation.getBlock().getChunk();
		if (chunk.isLoaded()) {
			Entity[] cEntities = chunk.getEntities();

			for (Entity ent : cEntities) {
				if ((ent instanceof Villager) && (ent.getLocation().getBlock().equals(inputLocation.getBlock()))) {
					entities.add((Villager)ent);
				}
			}

		}

		return entities;
	}

}
