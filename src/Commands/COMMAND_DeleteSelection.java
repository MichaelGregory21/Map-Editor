/**
 * Deletes all tiles beneath the selection mask
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

public class COMMAND_DeleteSelection implements Command {

    Set<Point> selectionRegion;
    Map<Point, Tile> regionTiles = new HashMap<>();
    Map<Point, Tile> maskAssignments = new HashMap<>();
    int shiftX, shiftY;
    SelectionMask mask;
    Layer layer;

    public COMMAND_DeleteSelection(SelectionMask mask) {
        this.mask = mask;
        layer = mask.getLayer();
        selectionRegion = new HashSet<>(mask.getRegion());
        shiftX = mask.getShiftX();
        shiftY = mask.getShiftY();

        for (Point point : selectionRegion) {
            // Store original tile beneath for undo
            regionTiles.put(point, layer.getTile(point.x, point.y));
            if (mask.isAssigned(point)) {
                maskAssignments.put(point, mask.contentsAt(point));
            }
        }
    }

    @Override
    public void execute() {
        for (Point point : selectionRegion) {
            if (mask.isAssigned(point)) {
                // Delete only the assigned tile
                mask.unassign(point);
            } else {
                // Delete underlying tile at shifted location
                int x = point.x + shiftX;
                int y = point.y + shiftY;
                layer.setTile(new Point(x, y), Tile.EMPTY_TILE);
            }
        }

        mask.clear();
    }

    @Override
    public void undo() {
        mask.clear();
        mask.addAll(selectionRegion);

        for (Point point : selectionRegion) {
            if (maskAssignments.containsKey(point)) {
                mask.assign(point, maskAssignments.get(point));
            } else {
                // Restore underlying tile at shifted location
                int x = point.x + shiftX;
                int y = point.y + shiftY;
                layer.setTile(new Point(x, y), regionTiles.get(point));
            }

            // Restore original tile at the original point (not shifted)
            layer.setTile(point, regionTiles.get(point));
        }

        mask.setShift(shiftX, shiftY);
    }

    @Override
    public boolean save() {
        return true;
    }
}
