package Tools;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.util.HashSet;
import java.util.Set;

import Commands.COMMAND_SelectArea;
import Panels.Canvas;
import Panels.Canvas.Point;
import Tiles.Tile;
import Panels.Layer;
import UI.SelectionMask;

public class TOOL_Selector extends SuperTool {
	private static final TOOL_Selector INSTANCE = new TOOL_Selector();

	private TOOL_Selector() {
	}

	public static TOOL_Selector getInstance(Canvas canvas) {
		INSTANCE.attachCanvas(canvas);
		return INSTANCE;
	}

	private int startCol, startRow;
	private boolean dragging = false;

	@Override
	public void start(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		startCol = col;
		startRow = row;
		dragging = true;
	}

	@Override
	public void apply(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		if (!dragging)
			return;
		canvas.repaint();

	}

	@Override
	public void finish(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		if (!dragging)
			return;
		dragging = false;

		Set<Point> rectRegion = computeRectangle(startCol, startRow, col, row);
		SelectionMask mask = canvas.getSelectionMask();
		Set<Point> current = new HashSet<>(mask.getRegion());

		Set<Point> modifiedRegion = new HashSet<>();

		boolean ctrl = (modifiers & InputEvent.CTRL_DOWN_MASK) != 0;
		boolean shift = (modifiers & InputEvent.SHIFT_DOWN_MASK) != 0;
		boolean alt = (modifiers & InputEvent.ALT_DOWN_MASK) != 0;

		if (mask.getShiftX() == 0 && mask.getShiftY() == 0) {
			if (ctrl) {
				// Union
				modifiedRegion.addAll(current);
				modifiedRegion.addAll(rectRegion);
			} else if (shift) {
				// Intersection
				modifiedRegion.addAll(current);
				modifiedRegion.retainAll(rectRegion);
			} else if (alt) {
				// XOR
				modifiedRegion.addAll(current);
				for (Point p : rectRegion) {
					if (!modifiedRegion.add(p)) {
						modifiedRegion.remove(p);
					}
				}
			} else {
				modifiedRegion = new HashSet<>(rectRegion);
			}
		} else {
			modifiedRegion = new HashSet<>(rectRegion);
		}

		canvas.executeCommand(new COMMAND_SelectArea(layer, mask, modifiedRegion));
		canvas.repaint();

	}

	@Override
	public Set<Point> getRegion(int col, int row, Tile target, Color brushColor) {
		if (!dragging) {
			Set<Point> single = new HashSet<>();
			single.add(new Point(col, row));
			return single;
		} else {
			return computeRectangle(startCol, startRow, col, row);
		}
	}

	private Set<Point> computeRectangle(int x1, int y1, int x2, int y2) {
		Set<Point> region = new HashSet<>();
		int minX = Math.min(x1, x2);
		int maxX = Math.max(x1, x2);
		int minY = Math.min(y1, y2);
		int maxY = Math.max(y1, y2);

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (x >= 0 && y >= 0 && x < canvas.getNumCols() && y < canvas.getNumRows()) {
					region.add(new Point(x, y));
				}
			}
		}
		return region;
	}

}
