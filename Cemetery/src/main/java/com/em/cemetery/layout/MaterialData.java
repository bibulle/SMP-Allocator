package com.em.cemetery.layout;

import org.bukkit.Material;

public class MaterialData {
	public Material material;
	public byte data;

	public MaterialData(Material material, int data) {
		this.material = material;
		this.data = (byte) data;
	}

	protected static final MaterialData GRASS = new MaterialData(Material.GRASS, 0);
	protected static final MaterialData AIR = new MaterialData(Material.AIR, 0);
	protected static final MaterialData RED_ROSE = new MaterialData(Material.RED_ROSE, 0);
	protected static final MaterialData YELLOW_FLOWER = new MaterialData(Material.YELLOW_FLOWER, 0);
	protected static final MaterialData LONG_GRASS_0 = new MaterialData(Material.LONG_GRASS, 0);
	protected static final MaterialData LONG_GRASS_1 = new MaterialData(Material.LONG_GRASS, 1);
	protected static final MaterialData LONG_GRASS_2 = new MaterialData(Material.LONG_GRASS, 2);

	protected static final MaterialData SOUL_SAND = new MaterialData(Material.SOUL_SAND, 0);
	protected static final MaterialData SLAB_0 = new MaterialData(Material.STEP, 0);
	protected static final MaterialData SLAB_1 = new MaterialData(Material.STEP, 1);
	protected static final MaterialData SLAB_2 = new MaterialData(Material.STEP, 2);
	protected static final MaterialData SLAB_3 = new MaterialData(Material.STEP, 3);
	protected static final MaterialData SLAB_4 = new MaterialData(Material.STEP, 4); // Brick not nice
	protected static final MaterialData SLAB_5 = new MaterialData(Material.STEP, 5);
	protected static final MaterialData COBBLESTONE = new MaterialData(Material.COBBLESTONE, 0);
	protected static final MaterialData MOSS_STONE = new MaterialData(Material.MOSSY_COBBLESTONE, 0);
	protected static final MaterialData STONE = new MaterialData(Material.STONE, 0);
	protected static final MaterialData SMOOTH_BRICK_0 = new MaterialData(Material.SMOOTH_BRICK, 0);
	protected static final MaterialData SMOOTH_BRICK_1 = new MaterialData(Material.SMOOTH_BRICK, 1);
	protected static final MaterialData SMOOTH_BRICK_2 = new MaterialData(Material.SMOOTH_BRICK, 2);
	protected static final MaterialData SMOOTH_BRICK_3 = new MaterialData(Material.SMOOTH_BRICK, 3);

	static MaterialData[] SLABS = { SLAB_0, SLAB_1, SLAB_2, SLAB_3, SLAB_5 };
	static MaterialData[] PILARS = { COBBLESTONE, MOSS_STONE, STONE, SMOOTH_BRICK_0, SMOOTH_BRICK_1, SMOOTH_BRICK_2, SMOOTH_BRICK_3 };

}