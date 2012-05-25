package com.em.profession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

public class Profession extends JavaPlugin {

	ProfessionListener thelistener = new ProfessionListener(this);

	// Properties
	Map<Location, ProfessionBlock> professionMap = new HashMap<Location, ProfessionBlock>();

	// Constant
	private static final List<Material> BLOCK_TYPES = new ArrayList<Material>();
	static {
		BLOCK_TYPES.add(Material.WOOD_PLATE);
		BLOCK_TYPES.add(Material.STONE_PLATE);
	}

	public void onDisable() {
		// reload the configuration
		reloadConfig();

		// Construct the data
		Map<String, String> professionSetS = new HashMap<String, String>();
		for (Location l : this.professionMap.keySet()) {
			professionSetS.put(convertLocation(l), professionMap.get(l).toTxt());
		}
		String professions = new Yaml().dump(professionSetS);

		// if data change, update and save
		if (!professions.equals(getConfig().getString("data", "{}"))) {
			getConfig().set("data", professions);

			saveConfig();
		}
	}

	public void onEnable() {

		getServer().getPluginManager().registerEvents(this.thelistener, this);

		@SuppressWarnings("unchecked")
		Map<String, String> professionSetS = (Map<String, String>) new Yaml().loadAs(getConfig().getString("data", "{}"), HashMap.class);
		for (String s : professionSetS.keySet()) {
			Location l = convertString(s);

			this.professionMap.put(l, new ProfessionBlock(null, professionSetS.get(s)));
		}

		getLogger().info(professionMap.size() + " professions");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!sender.hasPermission("profession.create")) {
			sender.sendMessage(ChatColor.RED + "You have no permission to use this command !!");
			return true;
		}

		try {
			Player player = (Player) sender;
			Block block = getPlayerTargetBlock(player);

			if (!BLOCK_TYPES.contains(block.getType())) {
				sender.sendMessage(ChatColor.RED + "This "+block.getType().toString().toLowerCase()+"is not in the list : " + getBlockTypesAsString());
				return true;
			}
			if (this.professionMap.containsKey(block.getLocation())) {
				sender.sendMessage(ChatColor.RED + "That " + block.getType().toString().toLowerCase() + " is already a profession "+ChatColor.GREEN +"("+this.professionMap.get(block.getLocation()).toTxt()+")");
				return true;
			}
			
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "You must set a profession");
				return true;
			}
			this.professionMap.put(block.getLocation(), new ProfessionBlock(player, args[0]));
			sender.sendMessage(ChatColor.GREEN + "Profession created");
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

	/**
	 * Get the targeted Block
	 * 
	 * @param player
	 * @return
	 */
	private Block getPlayerTargetBlock(Player player) {
		Block block = player.getTargetBlock(TRANSPARENT, 5);
		return block;
	}

	// define transparent blocks id
	private static final HashSet<Byte> TRANSPARENT = new HashSet<Byte>();
	static {
		TRANSPARENT.add((byte) Material.AIR.getId());
		TRANSPARENT.add((byte) Material.FENCE.getId());
		TRANSPARENT.add((byte) Material.FENCE_GATE.getId());
		TRANSPARENT.add((byte) Material.DETECTOR_RAIL.getId());
		TRANSPARENT.add((byte) Material.POWERED_RAIL.getId());
		TRANSPARENT.add((byte) Material.RAILS.getId());
		TRANSPARENT.add((byte) Material.REDSTONE_WIRE.getId());
		TRANSPARENT.add((byte) Material.TORCH.getId());
	}

	/**
	 * Get list of allowed block as String (to be trace of put in messaged)
	 * @return
	 */
	String getBlockTypesAsString() {
		String ret = "";
		
		for (Material m : BLOCK_TYPES) {
			ret += ", "+m.toString().toLowerCase();
		}
		
		ret.replaceFirst(", ", "");
		
		return ret;
	}
	

}
