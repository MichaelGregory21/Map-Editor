/**
 * Replaces the tiles at given set of points on the given layer according to the given map
 */
package Commands;

import java.util.HashMap;
import java.util.Set;

import Panels.Canvas.Point;
import Panels.Layer;
import Tiles.Tile;

public class COMMAND_Fill implements Command {

	Layer layer;
	Set<Point> points;
	HashMap<Point, Tile> newColors;
	HashMap<Point, Tile> oldColors;

	public COMMAND_Fill(Layer layer, Set<Point> points, HashMap<Point, Tile> newColors,
			HashMap<Point, Tile> oldColors) {
		this.layer = layer;
		this.points = points;
		this.newColors = newColors;
		this.oldColors = oldColors;
	}

	@Override
	public void execute() {
		for (Point point : points) {
			layer.setTile(point.x, point.y, newColors.get(point));
		}

	}

	@Override
	public void undo() {
		for (Point point : points) {
			layer.setTile(point.x, point.y, oldColors.get(point));
		}

	}

	@Override
	public boolean save() {
		return true;
	}
}
