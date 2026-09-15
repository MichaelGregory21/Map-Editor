/**
 * Copying the selected layer adds all tiles beneath the selection mask as well as the shift of the selection mask to the clipboard. This command cannot be undone
 */

package Commands;

import Utility.Clipboard;
import UI.SelectionMask;
import Panels.Canvas.Point;
import Panels.Layer;
import Tiles.Tile;

import java.util.HashMap;
import java.util.Map;

public class COMMAND_CopySelection implements Command {

    private Clipboard clipboard;
    private Map<Point, Tile> copiedPoints = new HashMap<>();
    private int shiftX, shiftY;

    public COMMAND_CopySelection(Clipboard clipboard, SelectionMask mask) {
    	clipboard.clear();
    	
        this.clipboard = clipboard;
        this.shiftX = mask.getShiftX() + 1;
        this.shiftY = mask.getShiftY() + 1;
        Layer layer = mask.getLayer();
        
        for (Point point : mask.getRegion()) {
        	copiedPoints.put(point, layer.getTile(point));
        }
    }

    @Override
    public void execute() {
        clipboard.add(copiedPoints, shiftX, shiftY);
    }

    @Override
    public void undo() {}

    @Override
    public boolean save() {
        return false;
    }
}
