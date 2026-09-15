package Panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import Events.*;
import Tiles.Tile;
import Tools.*;
import UI.SelectionMask;
import UI.TileCursor;
import Utility.Clipboard;
import Utility.EventBus;
import Utility.Stack;
import Commands.COMMAND_CopyLayer;
import Commands.COMMAND_CopySelection;
import Commands.COMMAND_CutSelection;
import Commands.COMMAND_DeleteLayer;
import Commands.COMMAND_DeleteSelection;
import Commands.COMMAND_DeselectMask;
import Commands.COMMAND_MergeLayer;
import Commands.COMMAND_NewLayer;
import Commands.COMMAND_PasteSelection;
import Commands.COMMAND_ReorderLayer;
import Commands.COMMAND_CommitSelectionMask;
import Commands.Command;

public class Canvas {
	// ------------------------------------------------------------------------------
	// CONSTANTS
	// ------------------------------------------------------------------------------
	public final static int TILE_SIZE = 24;

	// ------------------------------------------------------------------------------
	// CODES
	// ------------------------------------------------------------------------------

	private final SuperTool[] TOOLS = { TOOL_Brush.getInstance(this), TOOL_Bucket.getInstance(this),
			TOOL_Eraser.getInstance(this), TOOL_Hand.getInstance(this), TOOL_Selector.getInstance(this),
			TOOL_Wand.getInstance(this) };

	// ------------------------------------------------------------------------------
	// DIMENSIONS
	// ------------------------------------------------------------------------------
	private int width, height;
	private int numCols, numRows;

	// ------------------------------------------------------------------------------
	// MOUSE VARIABLES
	// ------------------------------------------------------------------------------
	private int trueMouseX, trueMouseY;
	private int mouseX, mouseY;
	private Set<Point> mouseRegion = new HashSet<>();

	// ------------------------------------------------------------------------------
	// PANEL & COMPONENTS
	// ------------------------------------------------------------------------------
	private JPanel canvasPanel=new JPanel(){private static final long serialVersionUID=1L;

	@Override public void paintComponent(Graphics g){super.paintComponent(g);Graphics2D g2=(Graphics2D)g;g2.scale(scrollRegion.getScale(),scrollRegion.getScale());draw(g2);}};
	private ScrollRegion scrollRegion;
	private TileCursor cursor = new TileCursor(TILE_SIZE);
	private ArrayList<Layer> layers = new ArrayList<>();
	private int currentLayer = -1;
	private Color color = new Color(231, 221, 250);
	private SelectionMask selectionMask;
	private Clipboard clipboard = new Clipboard();

	// ------------------------------------------------------------------------------
	// BUS
	// ------------------------------------------------------------------------------
	private EventBus bus;

	// ------------------------------------------------------------------------------
	// TOOLS
	// ------------------------------------------------------------------------------
	private SuperTool currentTool = TOOLS[0];
	private Color brushColor = Color.RED;
	private int brushType = TOOL_Brush.SQUARE;
	private int brushSize = 0;

	// ------------------------------------------------------------------------------
	// UNDO & REDO STACKS
	// ------------------------------------------------------------------------------
	private Stack<Command> undoStack = new Stack<>();
	private Stack<Command> redoStack = new Stack<>();

	// ------------------------------------------------------------------------------
	// NESTED CLASSES
	// ------------------------------------------------------------------------------
	public static class Point {
		public final int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Point))
				return false;
			Point p = (Point) o;
			return x == p.x && y == p.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

		public boolean adjacent(Point q) {
			return ((q.x == x - 1 && q.y == y) || (q.x == x + 1 && q.y == y) || (q.y == y - 1 && q.x == x)
					|| (q.y == y + 1 && q.x == x));
		}

		public static boolean adjacent(Point p, Point q) {
			return p.adjacent(q);
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";

		}
	}

	// ------------------------------------------------------------------------------
	// CONSTRUCTOR & SETUP
	// ------------------------------------------------------------------------------
	public Canvas(int numCols, int numRows, EventBus bus) {
		this.numCols = numCols;
		this.numRows = numRows;
		this.bus = bus;
		selectionMask = new SelectionMask(bus);
		setParameters();
		setEventListeners();
		enableMouseListener();
		enableKeyListener();
		createInitialLayer();

	}

	private void setParameters() {
		this.width = numCols * TILE_SIZE;
		this.height = numRows * TILE_SIZE;
		canvasPanel.setBackground(color);
		canvasPanel.setSize(new Dimension(width, height));
		scrollRegion = new ScrollRegion(canvasPanel, bus);
	}

	private void setEventListeners() {
		// TOOL EVENTS
		bus.subscribe(EVENT_ToolChanged.class, e -> setTool(e.toolId()));
		bus.subscribe(EVENT_ColorChanged.class, e -> setBrushColor(e.color()));
		bus.subscribe(EVENT_RequestBrushSizeChange.class, e -> {
			changeBrushSize(e.amount());
			bus.publish(new EVENT_BrushSizeSet(brushSize));
		});
		bus.subscribe(EVENT_RequestBrushSizeSet.class, e -> {
			brushSize = e.size();
			bus.publish(new EVENT_BrushSizeSet(brushSize));
		});
		bus.subscribe(EVENT_ShapeChanged.class, e -> setBrushType(e.shape()));

		// VIEW EVENTS
		bus.subscribe(EVENT_ChangeZoom.class, e -> zoom(e.zoomAmount()));
		bus.subscribe(EVENT_SetZoom.class, e -> setScale(e.zoom()));
		bus.subscribe(EVENT_CenterCanvas.class, _ -> center());

		// UNDO/REDO EVENTS
		bus.subscribe(EVENT_UndoCanvas.class, _ -> undo());
		bus.subscribe(EVENT_RedoCanvas.class, _ -> redo());

		// SELECT EVENT
		bus.subscribe(EVENT_DeselectMask.class, _ -> {
			executeCommand(new COMMAND_DeselectMask(selectionMask));
			repaint();
		});
		bus.subscribe(EVENT_SelectAll.class, _ -> {
			selectAll();
			repaint();
		});
		bus.subscribe(EVENT_CopyMask.class, _ -> {
			if (!selectionIsNull()) {
				executeCommand(new COMMAND_CopySelection(clipboard, selectionMask));
				repaint();
			}
		});
		bus.subscribe(EVENT_CutMask.class, _ -> {
			if (!selectionIsNull()) {
				if (getNumLayers() != 0)
					executeCommand(new COMMAND_CutSelection(clipboard, selectionMask));
				repaint();
			}
		});
		bus.subscribe(EVENT_PasteMask.class, _ -> {
			if (!clipboard.isEmpty()) {
				executeCommand(new COMMAND_PasteSelection(clipboard, selectionMask, getLayer(currentLayer)));
				repaint();
			}
		});

		// LAYER EVENTS
		bus.subscribe(EVENT_RequestSelectLayer.class, e -> currentLayer = layers.indexOf(e.layer()));
		bus.subscribe(EVENT_RequestNewLayer.class, _ -> executeCommand(new COMMAND_NewLayer(this)));
		bus.subscribe(EVENT_RequestSetLayerVisible.class, e -> {
			e.layer().setVisible(e.visible());
			repaint();
		});
		bus.subscribe(EVENT_RequestDeleteLayer.class, e -> {
			if (e.layer() != null) {
				executeCommand(new COMMAND_DeleteLayer(this, e.layer()));
			}
		});
		bus.subscribe(EVENT_RequestChangeLayerIndex.class, e -> {
			if (e.oldIndex() < layers.size() && e.newIndex() < layers.size()) {
				executeCommand(new COMMAND_ReorderLayer(this, e.oldIndex(), e.newIndex()));
			}
		});

		bus.subscribe(EVENT_RequestMergeLayer.class, e -> {
			if (e.index() > 0 && e.index() < layers.size()) {
				executeCommand(new COMMAND_MergeLayer(this, e.index() - 1, e.index()));
				currentLayer = Math.min(e.index() - 1, layers.size() - 1);
			}
		});

		bus.subscribe(EVENT_RequestCopyLayer.class, e -> {
			if (e.index() >= 0 && e.index() < layers.size()) {
				executeCommand(new COMMAND_CopyLayer(this, e.index()));
			}
		});
	}

	private void createInitialLayer() {
		executeCommand(new COMMAND_NewLayer(this));
		for (int col = 0; col < numCols; col++) {
			for (int row = 0; row < numRows; row++) {
				layers.get(0).setTile(col, row, new Tile(Color.WHITE));
			}
		}
		selectionMask.setLayer(getLayer(0));
		undoStack.clear();
	}

	// ------------------------------------------------------------------------------
	// MOUSE HANDLING
	// ------------------------------------------------------------------------------
	private void enableMouseListener() {
		MouseAdapter mouseHandler = new MouseAdapter() {
			private void updateMousePosition(MouseEvent e) {
				trueMouseX = (int) (e.getX() / scrollRegion.getScale());
				trueMouseY = (int) (e.getY() / scrollRegion.getScale());
				mouseX = trueMouseX / TILE_SIZE;
				mouseY = trueMouseY / TILE_SIZE;
				bus.publish(new EVENT_MousePosition(mouseX, mouseY, trueMouseX, trueMouseY));
			}

			private void updateCursor() {
				if (currentTool != null) {
					mouseRegion = currentTool.getRegion(mouseX, mouseY, getTile(mouseX, mouseY), brushColor);
					cursor.setRegion(mouseRegion);
				}
				canvasPanel.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				updateMousePosition(e);
				int mods = e.getModifiersEx();
				if (!layers.isEmpty() && currentLayer >= 0 && currentLayer < layers.size()) {
					currentTool.start(mouseX, mouseY, getTile(mouseX, mouseY), brushColor, layers.get(currentLayer),
							mods);
				}
				updateCursor();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				updateMousePosition(e);
				int mods = e.getModifiersEx();
				if (!layers.isEmpty() && currentLayer >= 0 && currentLayer < layers.size()) {
					currentTool.apply(mouseX, mouseY, getTile(mouseX, mouseY), brushColor, layers.get(currentLayer),
							mods);
				}
				updateCursor();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				updateMousePosition(e);
				int mods = e.getModifiersEx();
				if (!layers.isEmpty() && currentLayer >= 0 && currentLayer < layers.size()) {
					currentTool.finish(mouseX, mouseY, getTile(mouseX, mouseY), brushColor, layers.get(currentLayer),
							mods);
				}
				updateCursor();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				updateMousePosition(e);
				updateCursor();

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				canvasPanel.requestFocusInWindow();
			}
		};
		canvasPanel.addMouseListener(mouseHandler);
		canvasPanel.addMouseMotionListener(mouseHandler);
	}

	private void enableKeyListener() {
		canvasPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "commitSelection");
		canvasPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "deleteSelection");

		canvasPanel.getActionMap().put("commitSelection", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				executeCommand(new COMMAND_CommitSelectionMask(selectionMask));
				repaint();
			}
		});
		canvasPanel.getActionMap().put("deleteSelection", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				executeCommand(new COMMAND_DeleteSelection(selectionMask));
				repaint();
			}
		});
	}

	// ------------------------------------------------------------------------------
	// DRAW
	// ------------------------------------------------------------------------------
	private void draw(Graphics2D g2) {

		// CANVAS BACKGROUND COLORS
		Color trans1 = new Color(255, 231, 231);
		Color trans2 = new Color(231, 231, 255);

		drawBackground(g2, trans1, trans2);

		if (layers.isEmpty() || getCurrentLayer() == -1)
			return;

		int maskIndex = getIndexOfLayer(selectionMask.getLayer());

		// DRAW ALL LAYERS
		for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
			drawLayer(g2, layerIndex);
			if (layerIndex == maskIndex) {
				selectionMask.drawFill(g2, numCols, numRows, TILE_SIZE);
			}
		}

		// DRAW THE OUTLINE OF THE MASK LAST
		selectionMask.drawOutline(g2, numCols, numRows, TILE_SIZE);
		cursor.draw(g2);

	}

	private void drawEmpty(Graphics2D g2, int col, int row, Color trans1, Color trans2) {
		if ((row + col) % 2 == 0) {
			g2.setColor(trans1);
		} else {
			g2.setColor(trans2);
		}

		g2.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		g2.setColor(Color.WHITE);
		g2.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}

	private void drawLayer(Graphics2D g2, int layerIndex) {
		if (!layers.get(layerIndex).isVisible()) return;
		for (int col = 0; col < numCols; col++) {
			for (int row = 0; row < numRows; row++) {
				Tile tile = layers.get(layerIndex).getTile(col, row);
				if (!tile.isEmpty() && !coveredBySelectionMask(col, row, layerIndex)) {
					g2.setColor(tile.getColor());
					g2.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

				}
			}
		}
	}

	/**
	 * Checks if the point at the given column, row at the given layer should be covered by the selection mask
	 * @param col The column of the point you want to check
	 * @param row The row of the point you want to check
	 * @param layerIndex The layer index of the point you want to check
	 * @return False if the mask is not on the given layer index or hasn't assigned the given point. True, otherwise
	 */
	private boolean coveredBySelectionMask(int col, int row, int layerIndex) {
		if (layerIndex != getIndexOfLayer(selectionMask.getLayer())) return false;
		Point shiftedPoint = new Point(col - selectionMask.getShiftX(), row - selectionMask.getShiftY());
		return selectionMask.isAssigned(shiftedPoint);
	}

	private void drawBackground(Graphics2D g2, Color trans1, Color trans2) {
		for (int col = 0; col < numCols; col++) {
			for (int row = 0; row < numRows; row++) {
				drawEmpty(g2, col, row, trans1, trans2);
			}
		}

	}

	public void repaint() {
		canvasPanel.repaint();
	}

	private boolean isInBounds(int col, int row) {
		return col >= 0 && col < numCols && row >= 0 && row < numRows;
	}

	// ------------------------------------------------------------------------------
	// LAYER METHODS
	// ------------------------------------------------------------------------------

	public void newLayer(Layer layer) {
		layers.add(layer);
		int index = getNumLayers() - 1;
		bus.publish(new EVENT_NewLayerCreated(layer, index));
		setCurrentLayer(index);
		repaint();

	}

	public void deleteLayer(Layer layer) {
		int removedIndex = layers.indexOf(layer);
		if (removedIndex >= 0 && removedIndex < getNumLayers()) {
			layers.remove(layer);
			if (layers.isEmpty()) {
				currentLayer = -1;
			} else {
				currentLayer = Math.min(currentLayer, getNumLayers() - 1);
			}

			bus.publish(new EVENT_LayerDeleted(layer));
			repaint();
		}
	}

	public void deleteLayer(int index) {
		if (index >= 0 && index < getNumLayers()) {
			Layer layer = layers.get(index);
			deleteLayer(layer);
		}
	}

	public String getNewLayerName() {
		int index = 1;
		for (Layer layer : layers) {
			String name = layer.getName();
			if (name.matches("Layer \\d+")) {
				String numberPartAsString = name.substring(6);
				int numberPart = Integer.parseInt(numberPartAsString);
				if (numberPart >= index) {
					index = numberPart + 1;
				}
			}

		}
		return "Layer " + index;
	}

	public int getIndexOfLayer(Layer layer) {
		return layers.indexOf(layer);
	}

	public boolean containsLayer(Layer layer) {
		return layers.contains(layer);
	}

	public void insertLayer(Layer layer, int index) {
		layers.add(index, layer);
		bus.publish(new EVENT_NewLayerCreated(layer, index));
		setCurrentLayer(index);
		repaint();
	}

	public void swapLayers(int index1, int index2) {
		if (index1 >= 0 && index2 >= 0 && index1 < getNumLayers() && index2 < getNumLayers()) {
			Layer layer1 = layers.get(index1);
			Layer layer2 = layers.get(index2);

			layers.set(index1, layer2);
			layers.set(index2, layer1);

			bus.publish(new EVENT_LayersReordered(index1, index2));
			repaint();
		}

	}

	public void mergeLayers(int lowerIndex, int upperIndex) {
		if (lowerIndex >= 0 && lowerIndex < getNumLayers() && upperIndex > 0 && upperIndex < getNumLayers()
				&& lowerIndex < upperIndex) {
			Layer lowerLayer = layers.get(lowerIndex);
			Layer upperLayer = layers.get(upperIndex);
			for (int col = 0; col < numCols; col++) {
				for (int row = 0; row < numRows; row++) {
					Tile mergeTile = upperLayer.getTile(col, row);
					if (!mergeTile.isEmpty()) {
						lowerLayer.setTile(col, row, mergeTile);
					}
				}
			}
			deleteLayer(upperLayer);
		}
		repaint();

	}

	public Layer getLayer(int index) {
		return layers.get(index);
	}

	// ------------------------------------------------------------------------------
	// SELECT
	// ------------------------------------------------------------------------------

	public boolean selectionIsNull() {
		return selectionMask.isEmpty();
	}

	public void selectAll() {
		Set<Point> all = new HashSet<>();
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				all.add(new Point(x, y));
			}
		}
		selectionMask.setRegion(all);
		selectionMask.setLayer(getLayer(currentLayer));
		repaint();
	}

	public SelectionMask getSelectionMask() {
		return selectionMask;
	}

	public void setSelectionMask(SelectionMask selectionMask) {
		this.selectionMask = selectionMask;
		repaint();
	}

	// ------------------------------------------------------------------------------
	// COMMANDS
	// ------------------------------------------------------------------------------
	public void executeCommand(Command cmd) {
		cmd.execute();
		if (cmd.save()) {
			undoStack.push(cmd);
			redoStack.clear();
		}
	}

	// ------------------------------------------------------------------------------
	// UNDO & REDO METHODS
	// ------------------------------------------------------------------------------
	public void undo() {
		if (undoStack.isEmpty())
			return;
		Command cmd = undoStack.pop();
		cmd.undo();
		redoStack.push(cmd);
		repaint();
	}

	public void redo() {
		if (redoStack.isEmpty())
			return;
		Command cmd = redoStack.pop();
		cmd.execute();
		undoStack.push(cmd);
		repaint();
	}

	// ------------------------------------------------------------------------------
	// SCROLLING & ZOOMING
	// ------------------------------------------------------------------------------
	public void zoom(double zoomAmount) {
		scrollRegion.zoom(zoomAmount);
	}

	public void scrollUp(int scrollAmount) {
		scrollRegion.scrollUp(scrollAmount);
	}

	public void scrollRight(int scrollAmount) {
		scrollRegion.scrollRight(scrollAmount);
	}

	public void center() {
		scrollRegion.center();
	}

	// ------------------------------------------------------------------------------
	// GETTERS & SETTERS
	// ------------------------------------------------------------------------------
	public Tile getTile(int col, int row) {
		if (layers.isEmpty() || currentLayer < 0 || currentLayer >= layers.size()) {
			return null;
		}
		return getTile(col, row, layers.get(currentLayer));

	}

	public Tile getTile(int col, int row, Layer layer) {
		if (layers.isEmpty())
			return null;
		if (!isInBounds(col, row))
			return null;
		return layer.getTile(col, row);
	}

	public void setTile(int col, int row, Tile tile) {
		if (!isInBounds(col, row))
			return;
		layers.get(currentLayer).setTile(col, row, tile);
	}

	public int getBrushType() {
		return brushType;
	}

	public int getBrushSize() {
		return brushSize;
	}

	public void setTool(int index) {
		this.currentTool = TOOLS[index];
	}

	public void setScale(double scale) {
		scrollRegion.setScale(scale);
	}

	public void setBrushColor(Color brushColor) {
		this.brushColor = brushColor;
	}

	public void setBrushType(int brushType) {
		this.brushType = brushType;
	}

	public void changeBrushSize(int changeAmount) {
		brushSize = Math.min(Math.max(brushSize + changeAmount, 0), 10);
	}

	public JScrollPane getPanel() {
		return scrollRegion.getPanel();
	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumLayers() {
		return layers.size();
	}

	public int getCurrentLayer() {
		return currentLayer;
	}

	public void setCurrentLayer(int index) {
		currentLayer = Math.min(index, getNumLayers() - 1);
		if (currentLayer >= 0 && currentLayer < layers.size()) {
			bus.publish(new EVENT_LayerSelected(layers.get(index)));
		}
		repaint();
	}

}
