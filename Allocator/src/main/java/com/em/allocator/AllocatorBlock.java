package com.em.allocator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class AllocatorBlock {

	Location location;
	Material filter;
	BlockFace face;
	int power;

	public AllocatorBlock(Block block, Material filter, BlockFace face) {
		this.location = block.getLocation();
		this.power = block.getBlockPower();
		this.filter = filter;
		this.face = face;
	}

	@Override
	public String toString() {
		return "AllocatorBlock (filter : " + filter + ", facing : " + face + ")";
	}

	public String paramToText() {
		return filter + "," + face;
	}

	public static AllocatorBlock fromBlockAndParamString(Block block, String paramString) {
		String[] args = paramString.split(",");
		return new AllocatorBlock(block, Material.valueOf(args[0]), BlockFace.valueOf(args[1]));
	}
}
