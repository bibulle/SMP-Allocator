package com.em.cemetery;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

import com.em.cemetery.exception.HoleInThePathException;
import com.em.cemetery.exception.MateriaInThePathException;
import com.em.cemetery.exception.PathNotClearException;
import com.em.cemetery.layout.Clear;
import com.em.cemetery.layout.Fill;
import com.em.cemetery.layout.Layout;
import com.em.cemetery.layout.MaterialData;

public class Tomb implements Comparable<Tomb> {

	private Location location = null;
	private BlockFace blockFace = null;
	private String user = "";
	private int number = 0;
	private Date date = new Date();
	private String message = "";
	private boolean last = false;

	private static final Layout CLEARER = new Clear();
	private Fill filler = Fill.getFillRandom();

	public static final int TOMB_SIZE_ORTHO = 2;
	public static final int TOMB_SIZE_DIREC = 5;
	public static final int TOMB_SIZE_HEIGH = 3;

	/**
	 * Constructor
	 */
	public Tomb(Location aLocation, Vector aDirection) {
		this.location = aLocation.clone();

		// Calculate direction
		if (Math.abs(aDirection.getX()) > Math.abs(aDirection.getZ())) {
			if (aDirection.getX() > 0) {
				blockFace = BlockFace.EAST;
			} else {
				blockFace = BlockFace.WEST;
			}
		} else {
			if (aDirection.getZ() > 0) {
				blockFace = BlockFace.SOUTH;
			} else {
				blockFace = BlockFace.NORTH;
			}
		}

	}

	/**
	 * Constructor
	 */
	private Tomb(Location aLocation, String user, int number, Date date, String message, BlockFace blockFace, boolean last, Fill filler) {
		this.location = aLocation.clone();
		this.user = user;
		this.number = number;
		this.date = date;
		this.message = message;
		this.blockFace = blockFace;
		this.last = last;
		this.filler = filler;
	}

	public void setUser(String name, int number, String deathMessage) {
		this.user = name;
		this.number = number;
		this.date = new Date();
		this.message = deathMessage;
		this.last = true;
	}

	/**
	 * Get this as YAML string
	 * 
	 * @return
	 */
	public String paramToText() {
		String ret = "";
		ret += getUser() + "|";
		ret += getNumber() + "|";
		ret += getDate().getTime() + "|";
		ret += getMessage() + "|";
		ret += getBlockFace() + "|";
		ret += isLast() + "|";
		ret += filler.getKey();

		return ret;
	}

	/**
	 * Get this from an YAML string
	 * 
	 * @return
	 */
	public static Tomb fromLocationAndParamString(Location l, String string) {
		String[] params = string.split("[|]");

		// for (int i = 0; i < params.length; i++) {
		// System.out.println(i+" "+params[i]);
		// }
		if (params.length == 7) {
			String user = params[0];
			int number = Integer.parseInt(params[1]);
			Date date = new Date(Long.parseLong(params[2]));
			String message = params[3];
			BlockFace blockFace = BlockFace.valueOf(params[4]);
			boolean last = Boolean.parseBoolean(params[5]);
			Fill filler = Fill.getFill(params[6]);

			return new Tomb(l, user, number, date, message, blockFace, last, filler);
		}

		return null;
	}

	/** Block that can be erased during Tomb creation **/
	private static Set<Material> EMPTY_MATERIALS = new HashSet<Material>();
	static {
		EMPTY_MATERIALS.add(Material.AIR);
		EMPTY_MATERIALS.add(Material.CACTUS);
		EMPTY_MATERIALS.add(Material.DEAD_BUSH);
		EMPTY_MATERIALS.add(Material.FIRE);
		EMPTY_MATERIALS.add(Material.LEAVES);
		EMPTY_MATERIALS.add(Material.LONG_GRASS);
		EMPTY_MATERIALS.add(Material.MELON_STEM);
		EMPTY_MATERIALS.add(Material.PUMPKIN_STEM);
		EMPTY_MATERIALS.add(Material.RED_ROSE);
		EMPTY_MATERIALS.add(Material.SAPLING);
		EMPTY_MATERIALS.add(Material.SNOW);
		EMPTY_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
		EMPTY_MATERIALS.add(Material.TORCH);
		EMPTY_MATERIALS.add(Material.VINE);
		EMPTY_MATERIALS.add(Material.WEB);
		EMPTY_MATERIALS.add(Material.WHEAT);
		EMPTY_MATERIALS.add(Material.YELLOW_FLOWER);
	}
	/** Block that cannot be in the ground (added to EMPTY_MATERIALS **/
	private static Set<Material> NOT_GROUND_MATERIALS = new HashSet<Material>();
	static {
		NOT_GROUND_MATERIALS.addAll(EMPTY_MATERIALS);
		NOT_GROUND_MATERIALS.add(Material.WATER);
		NOT_GROUND_MATERIALS.add(Material.LAVA);
		NOT_GROUND_MATERIALS.add(Material.STATIONARY_WATER);
		NOT_GROUND_MATERIALS.add(Material.STATIONARY_LAVA);
	}

	/**
	 * Really create a tomb
	 * 
	 * @param tombToCreate
	 *          list of other tomb to create (around this one)
	 * @return has the tomb been created
	 */
	public boolean create(List<Tomb> tombToCreate) {

		// Check emptiness and ground
		try {
			checkPath();
		} catch (PathNotClearException e) {
			return false;
		}

		clear();

		// look for others tomb around
		Vector doubleOrthoDirection = getDirection().crossProduct(new Vector(0, 1, 0)).multiply(2);
		tombToCreate.add(new Tomb(getLocation().add(doubleOrthoDirection), getDirection()));
		tombToCreate.add(new Tomb(getLocation().subtract(doubleOrthoDirection), getDirection()));

		tombToCreate.add(new Tomb(getLocation().add(getDirection().multiply(8)), getDirection().multiply(-1)));

		// On this side, check between the tombs too
		Location l = getLocation().add(getDirection().multiply(-2));
		try {
			checkOneLine(l);
			tombToCreate.add(new Tomb(getLocation().add(getDirection().multiply(-4)), getDirection().multiply(-1)));
		} catch (PathNotClearException e) {
		}

		return true;
	}

	/**
	 * Check if we can construct a tomb there (no hole and the ground and nothing in the path)
	 * 
	 * @return the list of materials in the path
	 * @throws PathNotClearException
	 *           there is hole or materials in the path
	 */
	public void checkPath() throws PathNotClearException {
		Set<Material> onThePathMaterials = new HashSet<Material>();

		Vector direction = getDirection();

		// place the current point before the first line
		Location current = getLocation().clone();
		current.subtract(direction);
		current.subtract(direction);

		// System.out.println("Check Path");
		for (int i = 0; i <= TOMB_SIZE_DIREC; i++) {
			// System.out.println("Check Path " + i);
			current.add(direction);
			try {
				checkOneLine(current);
			} catch (MateriaInThePathException e) {
				onThePathMaterials.addAll(e.getOnThePathMaterials());
			}
		}

		if (!onThePathMaterials.isEmpty()) {
			throw new MateriaInThePathException(onThePathMaterials);
		}
	}

	/**
	 * Check one line (for hole and items in the path)
	 * 
	 * @param center
	 *          the center location of the line
	 * @return the list of materials in the path
	 * @throws PathNotClearException
	 *           there is hole or materials in the path
	 */
	private void checkOneLine(Location center) throws PathNotClearException {
		// The return list
		Set<Material> onThePathMaterials = new HashSet<Material>();

		// Calculate orthogonal vector and starting point
		Vector orthoDirection = getDirection().crossProduct(new Vector(0, 1, 0));
		Location current = center.clone().subtract(orthoDirection).subtract(orthoDirection);

		// System.out.println("---------------------");
		for (int i = 0; i <= TOMB_SIZE_ORTHO; i++) {
			current.add(orthoDirection);

			// Check the ground
			Block block = current.getBlock();
			// System.out.println(block.getType());
			if (NOT_GROUND_MATERIALS.contains(block.getType())) {
				// block.setType(Material.GRAVEL);
				throw new HoleInThePathException();
			}

			// Check the air above
			for (int j = 0; j < TOMB_SIZE_HEIGH; j++) {
				current.add(0, 1, 0);
				block = current.getBlock();
				// System.out.println(block.getType());
				if (!EMPTY_MATERIALS.contains(block.getType())) {
					onThePathMaterials.add(block.getType());
				}
			}

			// Go back to the ground to next point
			for (int j = 0; j < TOMB_SIZE_HEIGH; j++) {
				current.subtract(0, 1, 0);
			}

		}
		// System.out.println("---------------------");

		if (!onThePathMaterials.isEmpty()) {
			throw new MateriaInThePathException(onThePathMaterials);
		}

	}

	/**
	 * Clear a Tomb (just Grass in the ground and air, flower or long_grass upon it)
	 */
	public void clear() {

		applyLayout(CLEARER);

	}

	/**
	 * Fill a Tomb
	 */
	public void fill() {

		applyLayout(filler);

		// add the sign
		if (!getUser().trim().equals("")) {
			Location current = getLocation();
			current.add(getDirection().multiply(2));
			current.add(new Vector(0, 1, 0).multiply(filler.getSignHeight()));

			Block block = current.getBlock();
			block.setType(Material.WALL_SIGN);
			switch (blockFace) {
			case NORTH:
				block.setData((byte) 3);
				break;
			case SOUTH:
				block.setData((byte) 2);
				break;
			case EAST:
				block.setData((byte) 4);
				break;

			default:
				block.setData((byte) 5);
				break;
			}
			Sign state = (Sign) block.getState();

			String line = getUser() + " - " + number;

			if (line.length() > 15) {
				line = getUser().substring(0, getUser().length() - (line.length() - 15)) + " - " + number;
			}
			state.setLine(0, line);

			int MAX_LINE = 4;
			String[] words = message.split("[ ]");
			int cptLine = 1;
			line = "";
			for (int i = 0; (i < words.length && cptLine < MAX_LINE); i++) {
				if (line.length() + words[i].length() < 14) {
					line += " " + words[i];
				} else {
					state.setLine(cptLine, line);
					cptLine++;
					line = words[i];
				}
			}
			if ((line.length() != 0) && (cptLine < MAX_LINE)) {
				state.setLine(cptLine, line);
			}
			state.update();
		}
		
		// add the torch if isLast
		if (isLast()) {
			Location current = getLocation();
			current.add(getDirection().multiply(3));
			current.add(new Vector(0, 1, 0).multiply(1+filler.getSignHeight()));
			Block block = current.getBlock();
			block.setType(Material.TORCH);
			block.setData((byte)5);
		}

	}

	/**
	 * Apply a Layout to a tom
	 * 
	 * @param layout
	 */
	public void applyLayout(Layout layout) {
		// Calculate directions
		Vector direction = getDirection();
		Vector orthoDirection = getDirection().crossProduct(new Vector(0, 1, 0));
		Vector vertical = new Vector(0, 1, 0);

		// Get the corner (into the ground)
		Location current = getLocation();
		current.subtract(orthoDirection).subtract(direction);
		current.subtract(orthoDirection).subtract(direction);
		for (int k = 0; k <= Tomb.TOMB_SIZE_HEIGH; k++) {
			current.add(vertical);
		}

		// Foreach block of the cube
		for (int i = 0; i <= Tomb.TOMB_SIZE_DIREC; i++) {
			current.add(direction);
			for (int j = 0; j <= Tomb.TOMB_SIZE_ORTHO; j++) {
				current.add(orthoDirection);
				for (int k = TOMB_SIZE_HEIGH; k >= 0; k--) {
					current.subtract(vertical);

					Block block = current.getBlock();
					MaterialData md = layout.getMaterialData(i, j, k, isLast());
					if (md != null) {
						block.setType(md.material);
						block.setData(md.data);
					}
				}
				for (int k = 0; k <= Tomb.TOMB_SIZE_HEIGH; k++) {
					current.add(vertical);
				}
			}
			for (int j = 0; j <= Tomb.TOMB_SIZE_ORTHO; j++) {
				current.subtract(orthoDirection);
			}
		}

	}

	public Location getLocation() {
		if (location != null) {
			return location.clone();
		}
		return null;
	}

	public BlockFace getBlockFace() {
		return blockFace;
	}

	public Vector getDirection() {
		switch (blockFace) {
		case NORTH:
			return new Vector(0, 0, -1);
		case SOUTH:
			return new Vector(0, 0, 1);
		case EAST:
			return new Vector(1, 0, 0);

		default:
			return new Vector(0, 0, -1);
		}
	}

	public String getUser() {
		return user;
	}

	public boolean isEmpty() {
		return user.trim().equals("");
	}

	public int getNumber() {
		return number;
	}

	public Date getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	@Override
	public int compareTo(Tomb t) {
		if (t == null) {
			return 1;
		}

		if (getUser().compareTo(t.getUser()) != 0) {
			return getUser().compareTo(t.getUser());
		}

		if (getNumber() != t.getNumber()) {
			return getNumber() - t.getNumber();
		}

		if (getDate().compareTo(t.getDate()) != 0) {
			return getDate().compareTo(t.getDate());
		}

		// TODO Auto-generated method stub
		return 0;
	}

}
