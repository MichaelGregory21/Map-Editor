package Tools;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Commands.COMMAND_Fill;
import Commands.Command;
import Panels.Canvas;
import Panels.Canvas.Point;
import Tiles.Tile;
import Panels.Layer;

public class TOOL_Eraser extends SuperTool {

	public final static int SQUARE = 0;
	public final static int ROUND = 1;
	public final static int DIAMOND = 2;

	private static final TOOL_Eraser INSTANCE = new TOOL_Eraser();

	private TOOL_Eraser() {
	}

	public static TOOL_Eraser getInstance(Canvas canvas) {
		INSTANCE.attachCanvas(canvas);
		return INSTANCE;
	}

	private HashMap<Point, Tile> oldColors = new HashMap<>();
	private HashMap<Point, Tile> newColors = new HashMap<>();

	@Override
	public void start(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		oldColors.clear();
		newColors.clear();
		apply(col, row, target, null, layer, 0);
	}

	@Override
	public void apply(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		Set<Point> region = getRegion(col, row, target, brushColor);

		// If the selection mask is non empty and on this layer, then restrict tool use
		// to its region
		if (!canvas.getSelectionMask().isEmpty() && canvas.getSelectionMask().getLayer() == layer) {
			region.retainAll(canvas.getSelectionMask().getRegion());
		}

		for (Point p : region) {
			newColors.put(p, Tile.EMPTY_TILE);
			if (!oldColors.containsKey(p))
				oldColors.put(p, canvas.getTile(p.x, p.y, layer));
			canvas.setTile(p.x, p.y, Tile.EMPTY_TILE);
		}
	}

	@Override
	public void finish(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		if (newColors.keySet().isEmpty())
			return;
		HashMap<Point, Tile> oldColorsCopy = new HashMap<>(oldColors);
		HashMap<Point, Tile> newColorsCopy = new HashMap<>(newColors);
		Command command = new COMMAND_Fill(layer, newColorsCopy.keySet(), newColorsCopy, oldColorsCopy);
		canvas.executeCommand(command);
	}

	@Override
	public Set<Point> getRegion(int col, int row, Tile target, Color brushColor) {
		int size = canvas.getBrushSize();
		Set<Point> region = new HashSet<>();
		if (col < 0 || row < 0 || col >= canvas.getNumCols() || row >= canvas.getNumRows())
			return region;

		region.add(new Point(col, row));

		switch (canvas.getBrushType()) {
		case SQUARE:
			size++;
			int half = size / 2;
			for (int dx = -half; dx <= half; dx++) {
				for (int dy = -half; dy <= half; dy++) {
					int nx = col + dx, ny = row + dy;
					if (nx >= 0 && ny >= 0 && nx < canvas.getNumCols() && ny < canvas.getNumRows()) {
						region.add(new Point(nx, ny));
					}
				}
			}
			break;
		case ROUND:
			for (int dx = -size; dx <= size; dx++) {
				for (int dy = -size; dy <= size; dy++) {
					if (dx * dx + dy * dy <= size * size) {
						int nx = col + dx, ny = row + dy;
						if (nx >= 0 && ny >= 0 && nx < canvas.getNumCols() && ny < canvas.getNumRows()) {
							region.add(new Point(nx, ny));
						}
					}
				}
			}
			break;
		case DIAMOND:
			for (int dx = -size; dx <= size; dx++) {
				for (int dy = -size; dy <= size; dy++) {
					if (Math.abs(dx) + Math.abs(dy) <= size) {
						int nx = col + dx, ny = row + dy;
						if (nx >= 0 && ny >= 0 && nx < canvas.getNumCols() && ny < canvas.getNumRows()) {
							region.add(new Point(nx, ny));
						}
					}
				}
			}
			break;
		}
		return region;
	}
}
