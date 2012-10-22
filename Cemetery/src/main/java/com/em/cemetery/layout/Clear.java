package com.em.cemetery.layout;


public class Clear extends Layout {

	@Override
	public MaterialData getMaterialData(int direct, int ortho, int height, boolean isLast) {
		switch (height) {
		case 0:
			return MaterialData.GRASS;
		case 1:
			return getRandom(TALL_GRASS);
		case 2:
			return MaterialData.AIR;
		case 3:
			return MaterialData.AIR;

		default:
			break;
		}

		return null;
	}

	private MaterialData[] TALL_GRASS = { MaterialData.LONG_GRASS_0, MaterialData.LONG_GRASS_1, MaterialData.LONG_GRASS_1, MaterialData.LONG_GRASS_1, MaterialData.LONG_GRASS_1, MaterialData.LONG_GRASS_2, MaterialData.RED_ROSE, MaterialData.YELLOW_FLOWER, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR, MaterialData.AIR };
}
