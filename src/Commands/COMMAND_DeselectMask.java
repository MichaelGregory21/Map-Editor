/**
 * Clears the selection mask and discards all tile assignments to points in the mask, rather than committing them
 */
package Commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Panels.Canvas.Point;
import Tiles.Tile;
import UI.SelectionMask;

public class COMMAND_DeselectMask implements Command {
	
	SelectionMask mask;
	Set<Point> oldSelectionRegion;
	Map<Point, Tile> oldAssignments;
	int oldShiftX, oldShiftY;
	
	public COMMAND_DeselectMask(SelectionMask mask) {
		this.mask = mask;
		this.oldSelectionRegion = new HashSet<>(mask.getRegion());
		this.oldAssignments = new HashMap<>(mask.getAssignments());
		this.oldShiftX = mask.getShiftX();
		this.oldShiftY = mask.getShiftY();
	}

	@Override
	public void execute() {
		mask.clear(); 
	}

	@Override
	public void undo() {
		mask.setRegion(oldSelectionRegion);
		mask.setShift(oldShiftX, oldShiftY);
		for (Point point : oldSelectionRegion) {
			Tile assignment = oldAssignments.get(point);
			if (assignment != null) {
				mask.assign(point, oldAssignments.get(point));
			}
			
		}

	}

	@Override
	public boolean save() {
		return true;
	}

}
