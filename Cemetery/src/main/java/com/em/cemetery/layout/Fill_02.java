package com.em.cemetery.layout;


public class Fill_02 extends Fill {


	static {
		for (int i = 0; i < MaterialData.SLABS.length; i++) {
			for (int j = 0; j < MaterialData.PILARS.length; j++) {
				addFillType(new Fill_02(i, j));
			}
		}
	}

	public Fill_02(int slab, int pilar) {
		super(2, slab, pilar);
		pilar = pilar % MaterialData.PILARS.length;

		updateTable(pilar, LEVEL_1);
		updateTable(pilar, LEVEL_2);
	}


	
	private void updateTable(int pilar, MaterialData[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				if (table[i][j].equals(MaterialData.COBBLESTONE)) {
					table[i][j] = MaterialData.PILARS[pilar];
				}
			}
		}
	}

	@Override
	public MaterialData getMaterialData(int direct, int ortho, int height, boolean isLast) {
		switch (height) {
		case 0:
			return LEVEL_0[direct][ortho];
		case 1:
			// if isLast, add flowers
			if (isLast && LEVEL_1[direct][ortho].equals(MaterialData.AIR) && LEVEL_0[direct][ortho].equals(MaterialData.GRASS) && (Math.random() < 0.25)) {
				return MaterialData.RED_ROSE;
			}
			if (isLast && LEVEL_1[direct][ortho].equals(MaterialData.AIR) && LEVEL_0[direct][ortho].equals(MaterialData.GRASS) && (Math.random() < 0.25)) {
				return MaterialData.YELLOW_FLOWER;
			}
			return LEVEL_1[direct][ortho];
		case 2:
			return LEVEL_2[direct][ortho];
		case 3:
			return MaterialData.AIR;

		default:
			break;
		}
		// TODO Auto-generated method stub
		return null;
	}

	private MaterialData[][] LEVEL_0 = { { MaterialData.GRASS, MaterialData.GRASS, MaterialData.GRASS }, { MaterialData.GRASS, MaterialData.SOUL_SAND, MaterialData.GRASS }, { MaterialData.GRASS, MaterialData.SOUL_SAND, MaterialData.GRASS }, { MaterialData.GRASS, MaterialData.SOUL_SAND, MaterialData.GRASS }, { MaterialData.GRASS, MaterialData.GRASS, MaterialData.GRASS }, { MaterialData.GRASS, MaterialData.GRASS, MaterialData.GRASS }, };
	private MaterialData[][] LEVEL_1 = { { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.COBBLESTONE, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, };
	private MaterialData[][] LEVEL_2 = { { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.COBBLESTONE, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, };

	@Override
	public int getSignHeight() {
		return 2;
	}

}
