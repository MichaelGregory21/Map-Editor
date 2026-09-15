/**
 * A Selection Mask is a set of points "selectedRegion" together with a partial map "contentsAt" which assigns a subset of the region to some tiles. A Selection Mask also comes equipped with a layer referenced 
 * by a canvas for draw order. Drawing a Selection Mask creates a region containing the assigned tiles where appropriate covered by a translucent blue mask and bordered with a dotted blue line. 
 */

package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Events.EVENT_DeselectMask;
import Events.EVENT_LayerSelected;
import Panels.Canvas.Point;
import Panels.Layer;
import Tiles.Tile;
import Utility.EventBus;

public class SelectionMask {

	private final Color TRANS_BLUE = new Color(0, 120, 215, 80);
	private final Color BLUE = new Color(0, 120, 215);

	private Set<Point> selectedRegion = new HashSet<>();
	private HashMap<Point, Tile> contentsAt = new HashMap<>();
	private Layer layer;

	private int shiftX = 0;
	private int shiftY = 0;

	public SelectionMask(EventBus bus) {
		bus.subscribe(EVENT_LayerSelected.class, _ -> {
			bus.publish(new EVENT_DeselectMask());
		});
	}

	// -----------------------
	// Region management
	// -----------------------
	public boolean isEmpty() {
		return selectedRegion.isEmpty();
	}

	public boolean contains(Point point) {
		return selectedRegion.contains(point);
	}

	public void setRegion(Set<Point> newSelectedRegion) {
		selectedRegion = new HashSet<>(newSelectedRegion);
		shiftX = 0;
		shiftY = 0;
	}

	public Set<Point> getRegion() {
		return selectedRegion;
	}

	public void add(Point point) {
		selectedRegion.add(point);
	}

	public void addAll(Set<Point> points) {
		selectedRegion.addAll(points);
	}

	public void assign(Point point, Tile tile) {
		contentsAt.put(point, tile);
	}

	public void assignAll(Map<Point, Tile> assignment) {
		contentsAt.putAll(assignment);
	}

	public void unassign(Point point) {
		contentsAt.remove(point);
	}

	public boolean isAssigned(Point point) {
		return contentsAt.containsKey(point);
	}

	public Tile contentsAt(Point point) {
		return contentsAt.get(point);
	}

	public void clear() {
		selectedRegion.clear();
		contentsAt.clear();
		shiftX = 0;
		shiftY = 0;
	}
	
	public void remove(Point point) {
		unassign(point);
		selectedRegion.remove(point);
	}
	
	public void removeAll(Set<Point> points) {
		for (Point point : points) {
			remove(point);
		}
	}

	// -----------------------
	// Moving the selection
	// -----------------------
	public void move(int dx, int dy) {
		shiftX += dx;
		shiftY += dy;
	}

	public void setShift(int shiftX, int shiftY) {
		this.shiftX = shiftX;
		this.shiftY = shiftY;
	}

	public int getShiftX() {
		return shiftX;
	}

	public int getShiftY() {
		return shiftY;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Layer getLayer() {
		return layer;
	}

	public Map<Point, Tile> getAssignments() {
		return contentsAt;
	}

	// -----------------------
	// Drawing
	// -----------------------
	public void drawOutline(Graphics2D g2, int numCols, int numRows, int tileSize) {
		if (isEmpty())
			return;

		List<Line> edges = Line.computeOutline(selectedRegion, tileSize);
		float[] dash = { 4f, 4f };

		g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
		g2.setColor(BLUE);

		for (Line edge : edges) {
			edge.setPoint(edge.getPoint().x + shiftX, edge.getPoint().y + shiftY);
			edge.draw(g2, 0);
		}
	}

	public void drawFill(Graphics2D g2, int numCols, int numRows, int tileSize) {
		if (layer.isVisible()) {
			for (Point point : selectedRegion) {
				if (point.x >= 0 && point.x < numCols && point.y >= 0 && point.y < numRows) {
					Tile tile = contentsAt.get(point);
					if (tile != null) {
						if (tile.getColor() != null) {
							g2.setColor(tile.getColor());
							g2.fillRect((point.x + shiftX) * tileSize, (point.y + shiftY) * tileSize, tileSize,
									tileSize);
						}
					}
					// always overlay translucent selection
					g2.setColor(TRANS_BLUE);
					g2.fillRect((point.x + shiftX) * tileSize, (point.y + shiftY) * tileSize, tileSize, tileSize);
				}
			}
		}
	}
}
