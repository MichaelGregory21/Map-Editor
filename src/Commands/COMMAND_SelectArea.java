/**
 * Sets the selection mask to the given region and layer
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

public class COMMAND_SelectArea implements Command {

	private SelectionMask mask;

	private Layer newLayer;
	private Layer oldLayer;

	private Set<Point> newSelection;
	private Set<Point> oldSelection;
	private int oldShiftX;
	private int oldShiftY;

	private Map<Point, Tile> oldAssignments = new HashMap<>();
	private Map<Point, Tile> overwrittenTiles = new HashMap<>();

	public COMMAND_SelectArea(Layer layer, SelectionMask mask, Set<Point> newRegion) {
		this.newLayer = layer;
		this.mask = mask;

		this.oldSelection = new HashSet<Point>(mask.getRegion());
		this.newSelection = new HashSet<>(newRegion);

		this.oldShiftX = 0;
		this.oldShiftY = 0;
	}

	@Override
	public void execute() {

		// Save the layer the mask used to be on and assign current layer
		oldLayer = mask.getLayer();
		oldShiftX = mask.getShiftX();
		oldShiftY = mask.getShiftY();

		// For each point in the old selection, if it is assigned a tile: 1) save the
		// tile at this point and then overwrite it 2) save its assignment and then
		// clear it from the mask.
		for (Point point : oldSelection) {
			if (mask.isAssigned(point)) {

				Point shiftedPoint = new Point(point.x + mask.getShiftX(), point.y + mask.getShiftY());

				if (shiftedPoint.x >= 0 && shiftedPoint.x < oldLayer.getNumCols() && shiftedPoint.y >= 0
						&& shiftedPoint.y < oldLayer.getNumRows()) {
					overwrittenTiles.put(point, oldLayer.getTile(shiftedPoint));
					oldLayer.setTile(shiftedPoint, mask.contentsAt(point));
				}

				oldAssignments.put(point, mask.contentsAt(point));

			}
		}

		// Clear whatever was selected and declare new assignment
		mask.clear();
		mask.setLayer(newLayer);
		mask.addAll(newSelection);

	}

	@Override
	public void undo() {
		// For each point in this selection, if it was assigned a value at any point
		// between this selection and another action (e.g., initiated mask movement),
		// then commit these assignments
		for (Point point : mask.getRegion()) {
			if (mask.isAssigned(point)) {
				mask.getLayer().setTile(point, mask.contentsAt(point));
			}
		}

		// Clear the selection & all assignments and revert to old selection, shift, &
		// layer
		mask.clear();
		mask.addAll(oldSelection);
		mask.setShift(oldShiftX, oldShiftY);
		mask.setLayer(oldLayer);

		// For each point that was overwritten by the commitment that occurred upon
		// making a new selection: 1) revert this assignment 2) revert overwritten tile
		for (Point point : oldAssignments.keySet()) {
			mask.assign(point, oldAssignments.get(point));
			Point shiftedPoint = new Point(point.x + oldShiftX, point.y + oldShiftY);
			if (shiftedPoint.x >= 0 && shiftedPoint.x < oldLayer.getNumCols() && shiftedPoint.y >= 0
					&& shiftedPoint.y < oldLayer.getNumRows()) {
				oldLayer.setTile(shiftedPoint, overwrittenTiles.get(point));
			}
		}
	}

	@Override
	public boolean save() {
		return true;
	}

}
