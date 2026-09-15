/**
 * Cutting the selection adds all tiles beneath the selection mask as well as the shift of the selection mask to the clipboard then deletes all tiles in the selection mask region
 */

package Commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Panels.Canvas.Point;
import Panels.Layer;
import Tiles.Tile;
import UI.SelectionMask;
import Utility.Clipboard;

public class COMMAND_CutSelection implements Command {

	Clipboard clipboard;
	Set<Point> selectionRegion;
	Map<Point, Tile> regionTiles = new HashMap<>();
	Map<Point, Tile> maskAssignments = new HashMap<>();
	Map<Point, Tile> clipboardElements = new HashMap<>();
	int shiftX, shiftY;
	SelectionMask mask;
	Layer layer;

	public COMMAND_CutSelection(Clipboard clipboard, SelectionMask mask) {
		clipboard.clear();
		this.mask = mask;
		this.clipboard = clipboard;
		layer = mask.getLayer();
		selectionRegion = new HashSet<>(mask.getRegion());
		for (Point point : mask.getRegion()) {
			regionTiles.put(point, layer.getTile(point));
			if (mask.isAssigned(point)) {
				maskAssignments.put(point, mask.contentsAt(point));
			}
		}
		shiftX = mask.getShiftX();
		shiftY = mask.getShiftY();
	}

	@Override
	public void execute() {
		for (Point point : selectionRegion) {
			if (mask.isAssigned(point)) {
				clipboardElements.put(point, mask.contentsAt(point));
			} else {
				clipboardElements.put(point, layer.getTile(point));
				layer.setTile(point, Tile.EMPTY_TILE);
			}
		}
		clipboard.add(clipboardElements, shiftX, shiftY);
		mask.clear();
	}

	@Override
	public void undo() {
		clipboard.clear();
		mask.clear();
		mask.addAll(selectionRegion);
		for (Point point : selectionRegion) {
			layer.setTile(point, regionTiles.get(point));
		}
		for (Point point : maskAssignments.keySet()) {
			mask.assign(point, maskAssignments.get(point));
		}
		mask.setShift(shiftX, shiftY);
	}

	@Override
	public boolean save() {
		return true;
	}
}
