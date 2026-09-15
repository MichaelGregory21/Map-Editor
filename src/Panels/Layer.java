package Panels;

import java.util.Arrays;

import Panels.Canvas.Point;
import Tiles.Tile;

public class Layer {

	private int numCols;
	private int numRows;
	private Tile[] tiles;
	private int tileSize;
	private boolean isVisible = true;

	private String name;

	public Layer(int numCols, int numRows, String name, int tileSize) {
		this.numCols = numCols;
		this.numRows = numRows;
		this.name = name;
		this.tileSize = tileSize;
		tiles = new Tile[numCols * numRows];
		Arrays.fill(tiles, Tile.EMPTY_TILE);
	}

	public Layer clone() {
		Layer copy = new Layer(numCols, numRows, this.getName(), tileSize);
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				copy.setTile(col, row, this.getTile(col, row));
			}
		}
		return copy;
	}

	public Tile getTile(int col, int row) {
		return tiles[row * numCols + col];
	}
	
	public Tile getTile(Point point) {
		return getTile(point.x, point.y);
	}

	public void setTile(int col, int row, Tile tile) {
		tiles[row * numCols + col] = tile;
	}
	
	public void setTile(Point point, Tile tile) {
		setTile(point.x, point.y, tile);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public boolean isVisible() {
		return isVisible;
	}
	
	public int getNumCols() {
		return numCols;
	}
	
	public int getNumRows() {
		return numRows;
	}
}
