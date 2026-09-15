package Tools;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import Commands.COMMAND_MoveSelectionMask;
import Commands.COMMAND_SelectArea;
import Panels.Canvas.Point;
import Panels.Canvas;
import Panels.Layer;
import Tiles.Tile;
import UI.SelectionMask;

public class TOOL_Hand extends SuperTool {

	private int startCol, startRow;
	private int accumulatedDx, accumulatedDy;
	private int originalShiftX, originalShiftY;
	private static final TOOL_Hand INSTANCE = new TOOL_Hand();

	private TOOL_Hand() {
	}

	public static TOOL_Hand getInstance(Canvas canvas) {
		INSTANCE.attachCanvas(canvas);
		return INSTANCE;
	}

	@Override
	public void start(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		SelectionMask mask = canvas.getSelectionMask();

		// If no selection is made, assume user wants to move entire canvas contents
		if (canvas.selectionIsNull()) {
			Set<Point> entireRegion = new HashSet<Point>();
			for (int x = 0; x < canvas.getNumCols(); x++) {
				for (int y = 0; y < canvas.getNumRows(); y++) {
					entireRegion.add(new Point(x, y));
				}
			}
			canvas.executeCommand(new COMMAND_SelectArea(layer, mask, entireRegion));
		}

		// Assign any unassigned points according to region and erase it from canvas
		for (Point point : mask.getRegion()) {
			if (!mask.isAssigned(point) && point.x >= 0 && point.x < mask.getLayer().getNumCols() && point.y >= 0
					&& point.y < mask.getLayer().getNumRows()) {
				mask.assign(point, mask.getLayer().getTile(point));
				mask.getLayer().setTile(point, Tile.EMPTY_TILE);
			}
		}

		// Save the original selection offset in the event of undo
		originalShiftX = mask.getShiftX();
		originalShiftY = mask.getShiftY();

		// Save the original mouse offset
		startCol = col - originalShiftX;
		startRow = row - originalShiftY;

		// Initialize the accumulated change
		accumulatedDx = 0;
		accumulatedDy = 0;

	}

	@Override
	public void apply(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		SelectionMask mask = canvas.getSelectionMask();

		int dx = col - startCol;
		int dy = row - startRow;

		mask.setShift(dx, dy);

		accumulatedDx = dx;
		accumulatedDy = dy;

		canvas.repaint();
	}

	@Override
	public void finish(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		if (accumulatedDx != 0 || accumulatedDy != 0) {
			canvas.executeCommand(
					new COMMAND_MoveSelectionMask(canvas.getSelectionMask(), originalShiftX, originalShiftY));
		}

	}

	@Override
	public Set<Point> getRegion(int col, int row, Tile target, Color brushColor) {
		return new HashSet<>();
	}

}
