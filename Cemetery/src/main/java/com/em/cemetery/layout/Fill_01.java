package com.em.cemetery.layout;


public class Fill_01 extends Fill {

	static {
		for (int i = 0; i < MaterialData.SLABS.length; i++) {
			for (int j = 0; j < MaterialData.PILARS.length; j++) {
				Fill f = new Fill_01(i, j);
				addFillType(f);
			}
		}
		// addFillType("01_01", new Fill_01_01());
	}

	public Fill_01(int slab, int pilar) {
		super(1, slab, pilar);
		slab = slab % MaterialData.SLABS.length;
		pilar = pilar % MaterialData.PILARS.length;

		updateTable(slab, pilar, LEVEL_1);
		updateTable(slab, pilar, LEVEL_2);

	}

	private void updateTable(int slab, int pilar, MaterialData[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				if (table[i][j].equals(MaterialData.SLAB_1)) {
					table[i][j] = MaterialData.SLABS[slab];
				}
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
			return MaterialData.GRASS;
		case 1:
			// if isLast, add flowers
			if (isLast && LEVEL_1[direct][ortho].equals(MaterialData.AIR) && (Math.random() < 0.25)) {
				return MaterialData.RED_ROSE;
			}
			if (isLast && LEVEL_1[direct][ortho].equals(MaterialData.AIR) && (Math.random() < 0.25)) {
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

	private MaterialData[][] LEVEL_1 = { { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.SLAB_1, MaterialData.AIR },
			{ MaterialData.AIR, MaterialData.SLAB_1, MaterialData.AIR }, { MaterialData.AIR, MaterialData.SLAB_1, MaterialData.AIR }, { MaterialData.AIR, MaterialData.COBBLESTONE, MaterialData.AIR },
			{ MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, };
	private MaterialData[][] LEVEL_2 = { { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR },
			{ MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, { MaterialData.AIR, MaterialData.COBBLESTONE, MaterialData.AIR },
			{ MaterialData.AIR, MaterialData.AIR, MaterialData.AIR }, };

	@Override
	public int getSignHeight() {
		return 2;
	}

}
