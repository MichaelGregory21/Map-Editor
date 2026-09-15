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
import Utility.Stack;

public class TOOL_Bucket extends SuperTool {

    private static final TOOL_Bucket INSTANCE = new TOOL_Bucket();
    private TOOL_Bucket() {}
    public static TOOL_Bucket getInstance(Canvas canvas) {
        INSTANCE.attachCanvas(canvas);
        return INSTANCE;
    }

	@Override
	public void start(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		Set<Point> region = getRegion(col, row, target, brushColor);
		
		
		
		HashMap<Point, Tile> oldColors = new HashMap<>();
	    HashMap<Point, Tile> newColors = new HashMap<>();
	    
	    for (Point p : region) {
	    	oldColors.put(p, target);
	    	newColors.put(p, new Tile(brushColor));
	    }
		Command command = new COMMAND_Fill(layer, region, newColors, oldColors);
		canvas.executeCommand(command);
		
	}
	@Override
	public void apply(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {}
	@Override
	public void finish(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {}

    @Override
    public Set<Point> getRegion(int col, int row, Tile target, Color brushColor) {
        Set<Point> region = new HashSet<>();
        Set<Point> maskRegion = new HashSet<>(canvas.getSelectionMask().getRegion());
        
        if (col < 0 || row < 0 || col >= canvas.getNumCols() || row >= canvas.getNumRows())
            return region;

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(col, row));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            if (p.x < 0 || p.y < 0 || p.x >= canvas.getNumCols() || p.y >= canvas.getNumRows())
                continue;
            if (canvas.getTile(p.x, p.y).getColor() != target.getColor())
                continue;
            if (region.contains(p))
                continue;
            if (!maskRegion.contains(p) && canvas.getSelectionMask().getShiftX() == 0 && canvas.getSelectionMask().getShiftY() == 0 && !canvas.selectionIsNull())
                continue;

            region.add(p);
            stack.push(new Point(p.x + 1, p.y));
            stack.push(new Point(p.x - 1, p.y));
            stack.push(new Point(p.x, p.y + 1));
            stack.push(new Point(p.x, p.y - 1));
        }
        return region;
    }


}
