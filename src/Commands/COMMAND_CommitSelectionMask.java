/**
 * Committing the selection mask sets each tile beneath the selection mask and assigns it to the contents at the corresponding tile in the mask. 
 * If there is no assignment to a particular tile in the mask, then nothing happens beneath this point
 */

package Commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Panels.Canvas.Point;
import UI.SelectionMask;
import Panels.Layer;
import Tiles.Tile;

public class COMMAND_CommitSelectionMask implements Command {

	private SelectionMask mask;
	private Layer layer;

	private Set<Point> selectionRegion;
	private Map<Point, Tile> oldAssignments;
	private Map<Point, Tile> overwrittenPoints;
	private int shiftX;
	private int shiftY;

	public COMMAND_CommitSelectionMask(SelectionMask mask) {
		this.mask = mask;
		this.layer = mask.getLayer();

		selectionRegion = new HashSet<>(mask.getRegion());
		overwrittenPoints = new HashMap<>();
		oldAssignments = new HashMap<>();

		this.shiftX = mask.getShiftX();
		this.shiftY = mask.getShiftY();

	}

	@Override
	public void execute() {
		// For each point in the selected region, if the point is assigned, save the
		// tile at this point (after shifting the index according to the mask shift) and
		// then overwrite it
		for (Point point : selectionRegion) {
			if (mask.isAssigned(point)) {
				Point shiftedPoint = new Point(point.x + shiftX, point.y + shiftY);
				if (shiftedPoint.x >= 0 && shiftedPoint.x < layer.getNumCols() && shiftedPoint.y >= 0
						&& shiftedPoint.y < layer.getNumRows()) {
					overwrittenPoints.put(point, layer.getTile(shiftedPoint));
					layer.setTile(shiftedPoint.x, shiftedPoint.y, mask.contentsAt(point));
				}

				oldAssignments.put(point, mask.contentsAt(point));
			}
		}
		// Clear the mask
		mask.clear();
	}

	@Override
	public void undo() {
		// For each point in the selected region, add it back to the mask and give it
		// its old assignment if it had one
		for (Point point : selectionRegion) {
			mask.add(point);
			if (oldAssignments.keySet().contains(point)) {
				mask.assign(point, oldAssignments.get(point));
			}
		}

		// For each point that was overwritten, revert it to its old value
		for (Point point : overwrittenPoints.keySet()) {
			Point shiftedPoint = new Point(point.x + shiftX, point.y + shiftY);
			if (shiftedPoint.x >= 0 && shiftedPoint.x < layer.getNumCols() && shiftedPoint.y >= 0
					&& shiftedPoint.y < layer.getNumRows()) {
				layer.setTile(shiftedPoint, overwrittenPoints.get(point));
			}
		}
		mask.setShift(shiftX, shiftY);
	}

	@Override
	public boolean save() {
		return true;
	}
}