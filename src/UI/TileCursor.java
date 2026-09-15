package UI;

import Panels.Canvas.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileCursor {

	private int tileSize;

	private final Color cursorColor = Color.GRAY;

	private Set<Point> previewTiles = new HashSet<>();
	private List<Line> outlineEdges = new ArrayList<>();

	public TileCursor(int tileSize) {
		this.tileSize = tileSize;
	}

	public void setRegion(Set<Point> region) {
		this.previewTiles = region;
		this.outlineEdges = Line.computeOutline(region, tileSize);
	}

	public void clear() {
		previewTiles.clear();
		outlineEdges.clear();
	}

	public void draw(Graphics2D g2) {
		g2.setStroke(new BasicStroke(1));
		for (Line line : outlineEdges) {
			g2.setColor(cursorColor.brighter());
			line.draw(g2, 0);
			g2.setColor(cursorColor);
			line.draw(g2, 1);
		}
	}

}
