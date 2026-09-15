/**
 * Sets the selection mask to the contents and shift of the clipboard with the given layer
 */
package Commands;

import Panels.Canvas.Point;
import Panels.Layer;
import Tiles.Tile;
import UI.SelectionMask;
import Utility.Clipboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class COMMAND_PasteSelection implements Command {

	private SelectionMask mask;

	private Set<Point> oldRegion;
	private Map<Point, Tile> oldContentsAt;
	private int oldShiftX, oldShiftY;
	private Layer oldLayer;
	
	private Set<Point> region;
	private Map<Point, Tile> contentsAt;
	private int shiftX, shiftY;
	private Layer layer;

	public COMMAND_PasteSelection(Clipboard clipboard, SelectionMask mask, Layer layer) {
		this.mask = mask;
		this.layer = layer;
		region = new HashSet<>(clipboard.getTiles().keySet());
		contentsAt = new HashMap<>(clipboard.getTiles());
		shiftX = clipboard.getShiftX();
		shiftY = clipboard.getShiftY();
	}

	@Override
	public void execute() {		
		oldRegion = new HashSet<>(mask.getRegion());
		oldContentsAt = new HashMap<>(mask.getAssignments());
		oldShiftX = mask.getShiftX();
		oldShiftY = mask.getShiftY();
		oldLayer = mask.getLayer();
		
		mask.setRegion(region);
		mask.assignAll(contentsAt);
		mask.setShift(shiftX, shiftY);
		mask.setLayer(layer);
	}

	@Override
	public void undo() {
		mask.clear();
		mask.setRegion(oldRegion);
		mask.assignAll(oldContentsAt);
		mask.setShift(oldShiftX, oldShiftY);
		mask.setLayer(oldLayer);
	}

	@Override
	public boolean save() {
		return true;
	}
}
