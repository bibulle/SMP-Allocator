package com.em.profession;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class ProfessionBlock {

	Villager.Profession profession;
	
	public Villager.Profession getProfession() {
		return profession;
	}

	/**
	 * Create a new profession
	 * 
	 * @param block
	 *          the block
	 * @param face
	 *          the face
	 */
	public ProfessionBlock(Player player, String professionS) {
		try {
			//System.out.println(professionS);
			this.profession = Villager.Profession.valueOf(professionS.toUpperCase());
		} catch (IllegalArgumentException e) {
			if (player != null) {
			  player.sendMessage(ChatColor.RED + "Unknown profession : " + professionS.toUpperCase());
			} else {
				System.err.println("Unknown profession : " + professionS.toUpperCase());
			}
			
			int random = (int)(Villager.Profession.values().length * Math.random());
			this.profession = Villager.Profession.getProfession(random);
			if (player != null) {
				player.sendMessage(ChatColor.RED + "Choose a random : " + profession);
			} else {
				System.err.println("Choose a random : " + profession);
			}
		}
	}

	/**
	 * GEt the object as Txt
	 * @return
	 */
	public String toTxt() {
		return profession.toString();
	}

}
