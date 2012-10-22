package com.em.cemetery;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CemeteryListener implements Listener {

	Cemetery thePlugin;

	/**
	 * Constructor
	 **/
	public CemeteryListener(Cemetery plugin) {
		this.thePlugin = plugin;

	}

	/**
	 * On death
	 **/
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		thePlugin.fillTomb(event.getEntity().getName(),event.getDeathMessage());
		
	}


}
