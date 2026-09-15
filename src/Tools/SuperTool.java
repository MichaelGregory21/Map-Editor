package Tools;

import java.awt.Color;
import java.util.Set;
import Panels.Canvas;
import Panels.Canvas.Point;
import Panels.Layer;
import Tiles.Tile;

public abstract class SuperTool {

	protected Canvas canvas;

	protected SuperTool() {
	}

	public void attachCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * The effect that occurs upon the first mouse click
	 * 
	 * @param col        The column index that is clicked
	 * @param row        The row index that is clicked
	 * @param target     The tile that is clicked on
	 * @param brushColor The color of the brush
	 * @param layer      The currently selected layer
	 * @param modifiers  Any held modifiers (ctrl, alt, shift)
	 */
	abstract public void start(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers);

	/**
	 * The effect that occurs upon a mouse drag
	 * 
	 * @param col        The column index that is dragged over
	 * @param row        The row index that is dragged over
	 * @param target     The tile that is dragged over
	 * @param brushColor The color of the brush
	 * @param layer      The currently selected layer
	 * @param modifiers  Any held modifiers (ctrl, alt, shift)
	 */
	abstract public void apply(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers);

	/**
	 * The effect that occurs when the mouse is released
	 * 
	 * @param col        The column index that is released on
	 * @param row        The row index that is released on
	 * @param target     The tile that is released on
	 * @param brushColor The color of the brush
	 * @param layer      The currently selected layer
	 * @param modifiers  Any held modifiers (ctrl, alt, shift)
	 */
	abstract public void finish(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers);

	/**
	 * This returns the set of all points in the "area of effect". The tile cursor
	 * will fill this area for viewer feedback
	 * 
	 * @param col        The column index that the area is based on
	 * @param row        The row index that the area is based on
	 * @param target     The tile that the area is based on
	 * @param brushColor The color of the brush that the area is based on
	 * @return The AOE
	 */
	abstract public Set<Point> getRegion(int col, int row, Tile target, Color brushColor);
}
