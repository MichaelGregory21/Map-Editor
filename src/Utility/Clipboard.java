package Utility;

import Panels.Canvas.Point;
import Tiles.Tile;

import java.util.HashMap;
import java.util.Map;


public class Clipboard {
	private Map<Point, Tile> contentsAt = new HashMap<>();
	private int shiftX, shiftY;
	
	public void add(Map<Point, Tile> contentsAt, int shiftX, int shiftY) {
		this.contentsAt = contentsAt;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
	}
	
	public Map<Point, Tile> getTiles(){
		return contentsAt;
	}
	
	public int getShiftX() {
		return shiftX;
	}
	
	public int getShiftY() {
		return shiftY;
	}
	
	public void clear() {
		contentsAt.clear();
		shiftX = 0;
		shiftY = 0;
	}
	
	public boolean isEmpty() {
		return contentsAt.isEmpty();
	}
	
	
}
