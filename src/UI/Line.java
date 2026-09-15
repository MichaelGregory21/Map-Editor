package UI;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Panels.Canvas.Point;

public class Line{
	public enum Side {TOP, BOTTOM, LEFT, RIGHT};
	
	private Point point;
	private Side side;
	private int tileSize;
	
	public Line(Point point, Side side, int tileSize) {
		this.point = point;
		this.side = side;
		this.tileSize = tileSize;
	}
	
	public void draw(Graphics2D g2, int offset) {
		int x = point.x * tileSize + tileSize/2;
		int y = point.y * tileSize + tileSize/2;
		switch(side) {
		case Side.TOP:
			g2.drawLine(x - (tileSize / 2) - offset, y - (tileSize / 2) - offset, x + (tileSize / 2) + offset, y - (tileSize / 2) - offset);
			break;
		case Side.BOTTOM:
			g2.drawLine(x - (tileSize / 2) - offset, y + (tileSize / 2) + offset, x + (tileSize / 2) + offset, y + (tileSize / 2) + offset);
			break;
		case Side.LEFT:
			g2.drawLine(x - (tileSize / 2) - offset, y + (tileSize / 2) + offset, x - (tileSize / 2) - offset, y - (tileSize / 2) - offset);
			break;
		case Side.RIGHT:
			g2.drawLine(x + (tileSize / 2) + offset, y + (tileSize / 2) + offset, x + (tileSize / 2) + offset, y - (tileSize / 2) - offset);
			break;
		}
	}
	
	public static Line lineFor(Point p, Point q, int tileSize) {
		if (!Point.adjacent(p, q)) throw new IllegalStateException("p & q are not adjacent");
		if (p.x == q.x + 1) return new Line(p, Line.Side.LEFT, tileSize);
		if (p.x == q.x - 1) return new Line(p, Line.Side.RIGHT, tileSize);
		if (p.y == q.y + 1) return new Line(p, Line.Side.TOP, tileSize);
		if (p.y == q.y - 1) return new Line(p, Line.Side.BOTTOM, tileSize);
		return new Line(p, Line.Side.RIGHT, tileSize);
	}
	
	public static List<Line> computeOutline(Set<Point> region, int tileSize) {
		List<Line> edges = new ArrayList<>();
		int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
		
		for (Point point : region) {
			for (int[] direction : directions) {
				Point neighbor = new Point(point.x + direction[0], point.y + direction[1]);
				if (!region.contains(neighbor)) {
					edges.add(Line.lineFor(point, neighbor, tileSize));
				}
			}
		}
		return edges;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public void setPoint(int x, int y) {
		this.point = new Point(x,y);
	}

	public Point getPoint() {
		return point;
	}
}
