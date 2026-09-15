package Tools;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import Panels.Canvas.Point;
import Panels.Canvas;
import Panels.Layer;
import Tiles.Tile;
import UI.SelectionMask;
import Utility.Stack;

public class TOOL_Wand extends SuperTool {
	private static final TOOL_Wand INSTANCE = new TOOL_Wand();

	private TOOL_Wand() {
	}

	public static TOOL_Wand getInstance(Canvas canvas) {
		INSTANCE.attachCanvas(canvas);
		return INSTANCE;
	}

	@Override
	public void start(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
		SelectionMask mask = canvas.getSelectionMask();
		if (mask.getLayer() != layer)
			return;
		Set<Point> region = getRegion(col, row, target, brushColor);
		mask.setRegion(region);
		canvas.repaint();
	}

	@Override
	public void apply(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
	}

	@Override
	public void finish(int col, int row, Tile target, Color brushColor, Layer layer, int modifiers) {
	}

	@Override
	public Set<Point> getRegion(int col, int row, Tile target, Color brushColor) {
		Set<Point> region = new HashSet<>();
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

			region.add(p);
			stack.push(new Point(p.x + 1, p.y));
			stack.push(new Point(p.x - 1, p.y));
			stack.push(new Point(p.x, p.y + 1));
			stack.push(new Point(p.x, p.y - 1));
		}

		return region;
	}

}
