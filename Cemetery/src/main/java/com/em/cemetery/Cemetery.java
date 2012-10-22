package com.em.cemetery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.em.cemetery.exception.PathNotClearException;

public class Cemetery extends JavaPlugin {

	CemeteryListener thelistener = new CemeteryListener(this);

	// Properties
	Map<Location, Tomb> tombMap = new HashMap<Location, Tomb>();
	int reloadEveryMinutes = -1;
	int MAX_TOMB_IN_ONCE = 200;

	public void onDisable() {
		saveDatas();
	}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this.thelistener, this);

		loadDatas();

		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new DataReloader(), 10000L, 10000L);

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!sender.hasPermission("cemetery.create")) {
			sender.sendMessage(ChatColor.RED + "You have no permission to use this command !!");
			return true;
		}

		try {
			Player player = (Player) sender;
			Block block = getPlayerTargetBlock(player);

			if (args.length != 0) {
				// Add a Tomb
				if (args[0].equalsIgnoreCase("add")) {
					if (this.tombMap.containsKey(block.getLocation())) {
						player.sendMessage(ChatColor.RED + "There is already a tomb there (" + this.tombMap.get(block.getLocation()) + ")");
						return true;
					}

					Tomb tomb = new Tomb(block.getLocation(), player.getLocation().getDirection());

					// Check emptyness of the path
					try {
						tomb.checkPath();
					} catch (PathNotClearException e) {
						player.sendMessage(ChatColor.RED + "Nothing done, " + e.getMessage());
						return true;
					}

					// Really create the tomb
					List<Tomb> tombToCreate = new ArrayList<Tomb>();
					tombToCreate.add(tomb);

					int cpt = 0;
					while (!tombToCreate.isEmpty() && (cpt < MAX_TOMB_IN_ONCE)) {
						Tomb firstTomb = tombToCreate.get(0);
						tombToCreate.remove(firstTomb);
						// Check if it do not already exist, create it
						if (this.tombMap.get(firstTomb.getLocation()) == null) {
							if (firstTomb.create(tombToCreate)) {
								this.tombMap.put(firstTomb.getLocation(), firstTomb);
								cpt++;
							}
						}
					}

					player.sendMessage(ChatColor.GREEN + "" + cpt + " tomb(s) added !");
					saveDatas();

					return true;
				}
				// Load configuration
				if (args[0].equalsIgnoreCase("reload")) {
					loadDatas();
					player.sendMessage(ChatColor.GREEN + "" + tombMap.size() + " tombs reloaded");

					return true;
				}
				// Clear all "empty" Tombs
				if (args[0].equalsIgnoreCase("clear")) {
					List<Tomb> tombs = new ArrayList<Tomb>(this.tombMap.values());
					Collections.sort(tombs);

					int cpt_cleared = 0;
					int cpt_filled = 0;
					for (Tomb tomb : tombs) {
						if (tomb.isEmpty()) {
							tomb.clear();
							cpt_cleared++;
						} else {
							tomb.fill();
							cpt_filled++;
						}
					}

					player.sendMessage(ChatColor.GREEN + "" + cpt_cleared + " tombs cleared (and " + cpt_filled + " filled)");

					return true;
				}
				// Fill all "empty" Tombs
				if (args[0].equalsIgnoreCase("fill")) {
					List<Tomb> tombs = new ArrayList<Tomb>(this.tombMap.values());
					Collections.sort(tombs);

					int cpt_filled = 0;
					for (Tomb tomb : tombs) {
						tomb.fill();
						cpt_filled++;
					}

					player.sendMessage(ChatColor.GREEN + "" + cpt_filled + " tombs filled");

					return true;
				}

			}
		} catch (ClassCastException e) {
			sender.sendMessage("You can only use this command as a player!");
			return true;
		}

		usage(sender);
		return true;
	}

	void usage(CommandSender sender) {
		// |----------------------------------------------------------------|
		// |Command usage :
		// | /cemetery add :
		// | Add a tomb on the pointed place
		// |
		// | /allocator fill
		// | Fill all the unknown tombs
		// |
		// | /allocator clear :
		// | Clear all the unknown tombs
		// |----------------------------------------------------------------|
		sender.sendMessage(ChatColor.WHITE + "Command usage :");
		sender.sendMessage(ChatColor.GOLD + " /cemetery add" + ChatColor.WHITE + " : ");
		sender.sendMessage(ChatColor.WHITE + "          Add a tomb on the pointed place");
		sender.sendMessage(ChatColor.GOLD + " /cemetery fill" + ChatColor.WHITE + " :");
		sender.sendMessage(ChatColor.WHITE + "          Fill all the unknown tombs");
		sender.sendMessage(ChatColor.GOLD + " /cemetery clear" + ChatColor.WHITE + " :");
		sender.sendMessage(ChatColor.WHITE + "          Clear all the unknown tombs");
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
	}

	/**
	 * save Datas to configuration file
	 */
	public void saveDatas() {
		// reload the config
		reloadConfig();

		// Contruct the datas
		Map<String, String> tombMapS = new HashMap<String, String>();
		for (Location l : this.tombMap.keySet()) {
			tombMapS.put(convertLocation(l), this.tombMap.get(l).paramToText());
		}
		String tombsS = new Yaml().dump(tombMapS);

		// if data change, update and save
		if (!tombsS.equals(getConfig().getString("data", "{}"))) {
			getConfig().set("data", tombsS);
			getConfig().set("reloadEveryMinutes", reloadEveryMinutes);

			saveConfig();
		}
	}

	/**
	 * load Datas from configuration file (if needed)
	 */
	private void loadDatas() {

		// reload the config
		reloadConfig();

		int oldTombMapSize = tombMap.size();
		int oldReloadEveryMinutes = reloadEveryMinutes;

		tombMap = new HashMap<Location, Tomb>();
		@SuppressWarnings("unchecked")
		HashMap<String, String> tombMapS = (HashMap<String, String>) new Yaml().loadAs(getConfig().getString("data", "{}"), HashMap.class);
		if (tombMapS != null) {
			for (String s : tombMapS.keySet()) {
				Location l = convertString(s);
				this.tombMap.put(l, Tomb.fromLocationAndParamString(l, tombMapS.get(s)));
			}
		}

		reloadEveryMinutes = getConfig().getInt("reloadEveryMinutes", 10);

		String message = "";
		if (oldTombMapSize != tombMap.size()) {
			message += ", " + tombMap.size() + " tombs";
		}
		if (oldReloadEveryMinutes != reloadEveryMinutes) {
			message += ", " + "Reload every " + reloadEveryMinutes + " minute(s)";
		}
		if (message.length() != 0) {
			getLogger().info("Configuration reloaded.  (" + message.replaceFirst(", ", "") + ")");
		}
	}

	/**
	 * Class to reload the configuration
	 * 
	 */
	long lastReload = System.currentTimeMillis();

	private final class DataReloader implements Runnable {
		public void run() {
			long now = System.currentTimeMillis();

			if ((now - lastReload) > reloadEveryMinutes * 60 * 1000) {
				loadDatas();
				lastReload = now;
			}
		}
	}

	public void fillTomb(String name, String deathMessage) {

		name = StringUtils.capitalize(name);
		Tomb theTomb = null;
		int number = 1;

		List<Tomb> tombs = new ArrayList<Tomb>(this.tombMap.values());
		Collections.sort(tombs);

		int cptEmptyTomb = 0;
		for (Tomb tomb : tombs) {
			if (tomb.isEmpty()) {
				cptEmptyTomb++;
			} else {
				if ((cptEmptyTomb != 0) && (theTomb == null)) {
					int random = (int) Math.floor(Math.random() * cptEmptyTomb);
					theTomb = tombs.get(random);
				}
			}
			if ((theTomb == null) && tomb.getUser().equalsIgnoreCase(name)) {
				theTomb = tomb;
			}
			if (tomb.getUser().equalsIgnoreCase(name)) {
				tomb.setLast(false);
				tomb.fill();
				if (tomb.getNumber() >= number) {
					number = tomb.getNumber() + 1;
				}
			}
		}
		// No tomb, get the oldest
		if (theTomb == null) {
			Date d = new Date();
			for (Tomb tomb : tombs) {
				if (tomb.getDate().before(d)) {
					d = tomb.getDate();
					theTomb = tomb;
				}
			}
		}

		if (theTomb != null) {
			theTomb.setUser(name, number, deathMessage);
			theTomb.fill();
		}

		saveDatas();
	}

}
