package com.em.allocator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class AllocatorBlock {

	private Location location;
	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public Location getLocation() {
		return location;
	}

	public List<Material> getFilters() {
		return filters;
	}

	public BlockFace getFace() {
		return face;
	}

	private List<Material> filters;
	private BlockFace face;
	private int power;

	/**
	 * Create a new Allocator
	 * 
	 * @param block
	 *          the block
	 * @param filters
	 *          the filters
	 * @param face
	 *          the face
	 */
	public AllocatorBlock(Block block, List<Material> filters, BlockFace face) {
		this.location = block.getLocation();
		this.power = block.getBlockPower();

		if (filters == null) {
			filters = new ArrayList<Material>();
		}
		while ((filters.size() != 1) && filters.contains(Material.AIR)) {
			filters.remove(Material.AIR);
		}
		this.filters = filters;

		this.face = face;
		setFace(block, face);

	}

	public boolean hasNoFilter() {
		boolean ret = filters.isEmpty();

		ret = ret || ((filters.size() == 1) && filters.contains(Material.AIR));

		return ret;
	}

	/**
	 * Translate Filters to YAML String
	 * 
	 * @return
	 */
	public String filtersToString() {
		if (hasNoFilter()) {
			return "" + Material.AIR;
		} else {
			String ret = "";
			for (Material m : filters) {
				if (ret.length() == 0) {
					ret += m;
				} else {
					ret += "|" + m;
				}

			}
			return ret;
		}
	}

	/**
	 * Translate Filters from String
	 * 
	 * @return
	 */
	public static List<Material> filtersFromString(String filtersS) {
		
		List<Material> ret = new ArrayList<Material>();
		
		String[] filtersArray = filtersS.split("[|]");
		for (String string : filtersArray) {
			ret.add(Material.valueOf(string));
		}

		return ret;
	}

	/**
	 * Get this as YAML string
	 * 
	 * @return
	 */
	public String paramToText() {
		return filtersToString() + "," + face;
	}

	/**
	 * Get this from an YAML string
	 * 
	 * @return
	 */
	public static AllocatorBlock fromBlockAndParamString(Block block, String paramString) {
		String[] args = paramString.split(",");
		return new AllocatorBlock(block, filtersFromString(args[0]), BlockFace.valueOf(args[1]));
	}

	@Override
	public String toString() {
		String ret = "No filter";
		if (!hasNoFilter()) {
			ret = "filter :";
			for (Material m : filters) {
				ret += " " + m;
			}
		}
		return ret + ", facing : " + face;
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

	/**
	 * Returns true, if the item is allowed to pass
	 * 
	 * @param itemStack
	 * @return
	 */
	public boolean isPassingFilter(ItemStack itemStack) {
		return hasNoFilter() || getFilters().contains(itemStack.getType());
	}

}
