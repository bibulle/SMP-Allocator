package com.em.allocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

import com.em.allocator.item.ItemAllocatable;

public class AllocatorListener implements Listener {

	Allocator thePlugin;

	List<Material> powerList = new ArrayList<Material>();

	// List<Location> dupePreventer = new ArrayList<Location>();

	/**
	 * Constructor
	 **/
	public AllocatorListener(Allocator plugin) {
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
		if (this.thePlugin.allocatorMap.containsKey(event.getBlock().getLocation())) {
			// Remove it from the list
			this.thePlugin.allocatorMap.remove(event.getBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.GREEN + "Allocator removed!");
		}
	}

	/**
	 * On redstone change of a block
	 **/
	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		// if it's a Allocator
		if (this.thePlugin.allocatorMap.containsKey(event.getBlock().getLocation())) {
			//System.out.println("================== BlockRedstoneEvent");
			// Just do the job
			AllocatorBlock al = this.thePlugin.allocatorMap.get(event.getBlock().getLocation());
			if ((al.power == 0) && (event.getNewCurrent() > 0)) {

				// it is powered
				//Bukkit.getLogger().info("Powered !! " + al);

				// Check if we are doing duplication
				// for (Location l : this.dupePreventer) {
				// if (l.distance(event.getBlock().getLocation()) < 3.0D) {
				// return;
				// }
				// }
				// this.dupePreventer.add(event.getBlock().getLocation());

				// If no... do the job
				allocateItems(event, new Random());

			}

			al.power = event.getNewCurrent();
			//System.out.println("New power : "+al+" -> "+al.power);
		}
	}

	/**
	 * Handles all the item input/output
	 */
	private void allocateItems(BlockRedstoneEvent event, Random random) {
		Block b = event.getBlock();
		AllocatorBlock al = this.thePlugin.allocatorMap.get(b.getLocation());
		// get filter
		Material filter = al.filter;
		BlockFace face = al.face;

		// list of item to allocate and corresponding remover
		List<ItemAllocatable> inputItems = new ArrayList<ItemAllocatable>();

		// get direction of Input
		int dx = getDirectionX(face);
		int dy = getDirectionY(face);
		int dz = getDirectionZ(face);

		InventoryHolder inputContainer = getContainer(b, dx, dy, dz);

		// No Input-Container (get dropped items)
		if ((inputContainer == null) || (inputContainer.getInventory() == null)) {
			Location inputLocation = b.getLocation().add(0.5D + dx, 0.5D + dy, 0.5D + dz);
			inputItems = AllocatorInput.getRandomItemFromDropped(b.getWorld(), inputLocation, filter, thePlugin);

			// Input-Container
		} else {
			inputItems = AllocatorInput.getRandomItemFromContainer(inputContainer, random, b, filter, thePlugin);
		}
		// Bukkit.getLogger().info(inputItems.size() +
		// " Items to be transfered : "+inputItems);

		InventoryHolder outputContainer = getContainer(b, -dx, -dy, -dz);
		
		// No Output-Container (get dropped items)
		if (outputContainer == null) {
			Location outputLocation = b.getLocation().add(0.5D - dx, 0.5D - dy, 0.5D - dz);
			AllocatorOutput.outputItemToDropped(inputItems, b.getWorld(), outputLocation);

			// Output-Container
		} else {
			if (outputContainer instanceof Furnace) {
				AllocatorOutput.outputItemToFurnace(inputItems, (Furnace) outputContainer, inputContainer, thePlugin);
			} else if (outputContainer.getInventory() != null){
				AllocatorOutput.outputItemToContainer(inputItems, outputContainer, inputContainer, thePlugin);
			}
		}

		// After 3 tick remove the block form the dupPreventer
		// this.thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(this.thePlugin,
		// new DupePreventerRunnable(event), 3L);
	}

	/**
	 * Get the Container (or null if none.. dropped items)
	 * 
	 * @param b
	 * @param dx
	 * @param dy
	 * @param dz
	 * @param inputLocation
	 * @return
	 */
	private InventoryHolder getContainer(Block b, int dx, int dy, int dz) {

		InventoryHolder container = null;

		// get container :chest, ...
		Block target = b.getRelative(dx, dy, dz);
		BlockState craftB = target.getState();
		if (craftB instanceof InventoryHolder) {
			container = (InventoryHolder) craftB;
		} else {
			// Search for storage mine cart and such
			Location location = b.getLocation().add(0.5D + dx, 0.5D + dy, 0.5D + dz);
			Arrow a = b.getWorld().spawnArrow(location, new Vector(0, 0, 0), 0.0F, 0.0F);
			for (Entity e : a.getNearbyEntities(0.5D, 0.5D, 0.5D)) {
				if (!(e instanceof StorageMinecart)) {
					continue;
				}
				container = (StorageMinecart) e;
			}
			a.remove();
		}
		
		// avoid dropping to rail
		if (container == null) {
			if ((target.getType() == Material.RAILS) || (target.getType() == Material.DETECTOR_RAIL) || (target.getType() == Material.POWERED_RAIL)) {
				container = new InventoryHolder() {
					public Inventory getInventory() {
						return null;
					}
				};
			}
		}

		return container;
	}

	/**
	 * Returns orientation along the x-axis (-1, 0, 1)
	 */
	private int getDirectionX(BlockFace blockFace) {
		int dx = 0;
		switch (blockFace) {
		case EAST:
			dx = -1;
			break;
		case WEST:
			dx = 1;
			break;
		default:
			break;
		}

		return dx;
	}

	/**
	 * Returns orientation along the y-axis (-1, 0, 1)
	 */
	private int getDirectionY(BlockFace blockFace) {
		int dy = 0;
		switch (blockFace) {
		case UP:
			dy = -1;
			break;
		case DOWN:
			dy = 1;
			break;
		default:
			break;
		}

		return dy;
	}

	/**
	 * Returns orientation along the z-axis (-1, 0, 1)
	 */
	private int getDirectionZ(BlockFace blockFace) {
		int dz = 0;
		switch (blockFace) {
		case SOUTH:
			dz = -1;
			break;
		case NORTH:
			dz = 1;
			break;
		default:
			break;
		}

		return dz;
	}

	/**
	 * Class to remove block from the dupePreventer
	 */
	// public class DupePreventerRunnable implements Runnable {
	// BlockPhysicsEvent event;
	//
	// DupePreventerRunnable(BlockPhysicsEvent event) {
	// this.event = event;
	// }
	//
	// public void run() {
	// AllocatorListener.this.dupePreventer.remove(this.event.getBlock().getLocation());
	// }
	// }

}
