package com.em.chesttrap;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

public class ChestTrap extends JavaPlugin {

	ChestTrapListener thelistener = new ChestTrapListener(this);

	// Properties
	Map<Location, String> chestMap = new HashMap<Location, String>();

	// Constant
	Material BLOCK_TYPE = Material.CHEST;

	public void onDisable() {
		// reload the config
		reloadConfig();

		// Contruct the datas
		Map<String, String> chestMapS = new HashMap<String, String>();
		for (Location l : this.chestMap.keySet()) {
			chestMapS.put(convertLocation(l), this.chestMap.get(l));
		}
		String chests = new Yaml().dump(chestMapS);

		// if data change, update and save
		if (!chests.equals(getConfig().getString("data", "{}"))) {
			getConfig().set("data", chests);

			saveConfig();
		}
	}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this.thelistener, this);

		@SuppressWarnings("unchecked")
		HashMap<String, String> chestMapS = (HashMap<String, String>) new Yaml().loadAs(getConfig().getString("data", "{}"), HashMap.class);
		for (String s : chestMapS.keySet()) {
			Location l = convertString(s);
			this.chestMap.put(l, "YES");
		}

		getLogger().info(chestMap.size() + " chests");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!sender.hasPermission("chests.create")) {
			return false;
		}
		try {
			Player player = (Player) sender;
			Block block = player.getTargetBlock(null, 5);
			if (!block.getType().equals(BLOCK_TYPE)) {
				sender.sendMessage(ChatColor.RED + "Either that's not a " + BLOCK_TYPE.toString().toLowerCase() + ", you're too far away, or there's a non-full block in the way.");
				return true;
			}
			if (this.chestMap.containsKey(block.getLocation())) {
				sender.sendMessage(ChatColor.RED + "That " + BLOCK_TYPE.toString().toLowerCase() + " is already an allocator ! (filter : " + this.chestMap.get(block.getLocation()) + ")");
				return true;
			}

			this.chestMap.put(block.getLocation(), "YES");
			sender.sendMessage(ChatColor.GREEN + "Chest added ! (" + block.getLocation() + ")");
		} catch (ClassCastException e) {
			sender.sendMessage("You can only use this command as a player!");
		}
		return true;
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
