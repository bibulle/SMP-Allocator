package com.em.allocator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

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

		setFace(block, face);

	}

	@Override
	public String toString() {
		String ret = "No filter";
		if (!filter.equals(Material.AIR)) {
			ret = "filter : " + filter;
		}
		return ret + ", facing : " + face;
	}

	public String paramToText() {
		return filter + "," + face;
	}

	public static AllocatorBlock fromBlockAndParamString(Block block, String paramString) {
		String[] args = paramString.split(",");
		return new AllocatorBlock(block, Material.valueOf(args[0]), BlockFace.valueOf(args[1]));
	}

	/**
	 * Get face from Block (it's strange to redefine it...)
	 * 
	 * @param block
	 * @return
	 */
	public static BlockFace getFace(Block block) {
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
		// face = ((Directional) block.getState().getData()).getFacing();
		return face;
	}

	/**
	 * Set face to Block (it's strange to redefine it...)
	 * 
	 * @param block
	 * @return
	 */
	public static void setFace(Block block, BlockFace face) {

		switch (face) {
		case SOUTH:
		default:
			block.setData((byte) 0);
			break;
		case WEST:
			block.setData((byte) 1);
			break;
		case NORTH:
			block.setData((byte) 2);
			break;
		case EAST:
			block.setData((byte) 3);
			break;
		case UP:
			block.setData((byte) 4);
			break;
		case DOWN:
			block.setData((byte) 5);
			break;

		}
	}

}
