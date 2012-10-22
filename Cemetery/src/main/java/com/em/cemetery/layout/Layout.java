package com.em.cemetery.layout;


public abstract class Layout {
	
	/**
	 * Get MaterialData to use at this tomb point
	 * @param isLast 
	 * @param i coordinate in "direction"
	 * @param j coordinate in "ortho direction"
	 * @param k coordinate in "height direction"
	 * @return the Material data to use
	 */
	public abstract MaterialData getMaterialData(int direct, int ortho, int height, boolean isLast);
	
	
	/**
	 * get a randomize material in a table
	 * @param table a table of materialdatas
	 * @return
	 */
	protected MaterialData getRandom(MaterialData[] table) {
		int max = table.length;
		int random = (int) Math.floor(Math.random() * max);
		return table[random];
	}


}
