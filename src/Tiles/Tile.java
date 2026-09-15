/**
 * A tile is simply a color and a square dimension for now. Later, the tile will be assigned the following:
 * - BufferedImage image // The actual image displayed on this tile. May be affected by neighbors or animation frame
 * - boolean connected // Does this tile change its image based on the tiles around it
 * - boolean animated // Does this tile loop through an animation
 * - boolean shaded // Does this tile display part of a large connected texture that spans the window
 * Note that a tile does not come equipped with a coordinate. This is a deliberate choice to allow the canvas (layers) to handle the tile placement to enforce uniqueness of tile locations
 */

package Tiles;

import java.awt.Color;
import java.util.Objects;

public class Tile {

	public final static Tile EMPTY_TILE = new Tile(null);

	Color color;

	public Tile(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public boolean isEmpty() {
		return color == null;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tile))
			return false;
		Tile t = (Tile) o;
		return color == t.color;
	}

	@Override
	public int hashCode() {
		return Objects.hash(color);
	}
}
