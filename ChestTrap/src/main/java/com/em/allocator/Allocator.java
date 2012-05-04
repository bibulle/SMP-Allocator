package com.em.allocator;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

public class Allocator extends JavaPlugin {

	AllocatorListener thelistener = new AllocatorListener(this);

	// Properties
	Map<Location, AllocatorBlock> allocatorMap = new HashMap<Location, AllocatorBlock>();
	public boolean allowFiltering = true;
	public int quantityDropped = 2;
	public boolean quantityIsStack = true;

	// Constant
	Material BLOCK_TYPE = Material.PUMPKIN;

	public void onDisable() {
		// reload the config
		reloadConfig();

		// Contruct the datas
		Map<String, String> allocatorMapS = new HashMap<String, String>();
		for (Location l : this.allocatorMap.keySet()) {
			allocatorMapS.put(convertLocation(l), this.allocatorMap.get(l).paramToText());
		}
		String allocators = new Yaml().dump(allocatorMapS);

		// if data change, update and save
		if (!allocators.equals(getConfig().getString("data", "{}"))) {
			getConfig().set("data", allocators);

			saveConfig();
		}
	}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this.thelistener, this);

		@SuppressWarnings("unchecked")
		HashMap<String, String> allocatorMapS = (HashMap<String, String>) new Yaml().loadAs(getConfig().getString("data", "{}"), HashMap.class);
		for (String s : allocatorMapS.keySet()) {
			Location l = convertString(s);
			Block b = l.getBlock();
			this.allocatorMap.put(l, AllocatorBlock.fromBlockAndParamString(b, allocatorMapS.get(s)));
		}

		allowFiltering = getConfig().getBoolean("allowFiltering", true);
		quantityDropped = getConfig().getInt("quantityDropped", 1);
		quantityIsStack = getConfig().getBoolean("quantityIsStack", true);

		getLogger().info(allocatorMap.size() + " allocators");
		getLogger().info("allowFiltering  = " + allowFiltering);
		getLogger().info("quantityDropped = " + quantityDropped);
		getLogger().info("quantityIsStack = " + quantityIsStack);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!sender.hasPermission("allocator.create")) {
			return false;
		}
		try {
			Player player = (Player) sender;
			Block block = player.getTargetBlock(null, 5);
			Material filter = player.getItemInHand().getType();
			if (!block.getType().equals(BLOCK_TYPE)) {
				sender.sendMessage(ChatColor.RED + "Either that's not a " + BLOCK_TYPE.toString().toLowerCase() + ", you're too far away, or there's a non-full block in the way.");
				return true;
			}
			if (this.allocatorMap.containsKey(block.getLocation())) {
				sender.sendMessage(ChatColor.RED + "That " + BLOCK_TYPE.toString().toLowerCase() + " is already an allocator ! (filter : " + this.allocatorMap.get(block.getLocation()) + ")");
				return true;
			}

			// create a new Block
			BlockFace face = BlockFace.NORTH;
			if ((args.length != 0) && (BlockFace.valueOf(args[0]) != null)) {
				face = BlockFace.valueOf(args[0]);
			} else if (block.getState().getData() instanceof Directional) {
				face = getFace(block);
			}
			
			AllocatorBlock al = new AllocatorBlock(block, filter, face);
			this.allocatorMap.put(block.getLocation(), al);
			sender.sendMessage(ChatColor.GREEN + "Allocator added ! (" + al + ")");
		} catch (ClassCastException e) {
			sender.sendMessage("You can only use this command as a player!");
		}
		return true;
	}

	/**
	 * Get face from Block (it's strange to redefine it...)
	 * @param block
	 * @return
	 */
	private BlockFace getFace(Block block) {
		BlockFace face;
		switch (block.getData()) {
		case 0:
		default:
			face = BlockFace.SOUTH;
			break;
		case 1:
			face = BlockFace.WEST;
			break;
		case 2:
			face = BlockFace.NORTH;
			break;
		case 3:
			face = BlockFace.EAST;
			break;

		}
		//face = ((Directional) block.getState().getData()).getFacing();
		return face;
	}

	String convertLocation(Location l) {
		String out = "";
		out = out + l.getWorld().getName() + ",";
		out = out + l.getBlockX() + ",";
		out = out + l.getBlockY() + ",";
		out = out + l.getBlockZ();
		return out;
	}

	Location convertString(String s) {
		String[] parts = s.split(",");
		return new Location(getServer().getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
	}
}
