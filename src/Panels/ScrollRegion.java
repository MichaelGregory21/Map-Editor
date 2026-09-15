package Panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import Events.EVENT_ZoomChanged;
import Utility.EventBus;

public class ScrollRegion {
	private static final double ZOOM_STEP = 0.1;
	private static final double MIN_SCALE = 0.2;
	private static final double MAX_SCALE = 3.0;
	private static final int BORDER_SIZE = 400;

	// DIMENSIONS
	private int width, height;
	private int panelWidth, panelHeight;

	// COMPONENTS
	private JScrollPane scrollPane;
	private JPanel container;
	private JPanel panel;
	private EventBus bus;

	// TIMER FOR SCROLLING
	private Timer scrollTimer;

	// COLOR
	private Color borderColor = new Color(227, 227, 240);
	private double scale = 1.0;

	public ScrollRegion(JPanel panel, int panelWidth, int panelHeight, EventBus bus) {
		this.panel = panel;
		this.container = new JPanel(null);
		this.scrollPane = new JScrollPane();
		this.bus = bus;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		this.width = panelWidth + (2 * BORDER_SIZE);
		this.height = panelHeight + (2 * BORDER_SIZE);
		setParameters();
		enableResizing();
		enableArrowKeyScrolling();
		rescale(scale);
	}

	public ScrollRegion(JPanel panel, EventBus bus) {
		this(panel, panel.getWidth(), panel.getHeight(), bus);
	}

	private void setParameters() {
		panel.setBounds(BORDER_SIZE, BORDER_SIZE, panelWidth, panelHeight);
		panel.setDoubleBuffered(true);

		container.setBackground(borderColor);
		container.setPreferredSize(new Dimension(width, height));
		container.add(panel);
		container.setDoubleBuffered(true);

		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(50);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
		scrollPane.setViewportView(container);
		scrollPane.setDoubleBuffered(true);
	}

	private void enableResizing() {
		scrollPane.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isControlDown()) {
					// Ignore panning when control is held
					scrollPane.setWheelScrollingEnabled(false);

					// Calculate zoom factor and adjust scale accordingly
					double oldScale = scale;
					double zoomFactor = (e.getWheelRotation() < 0) ? 1 + ZOOM_STEP : 1 - ZOOM_STEP;
					double minScale = getMinScale();
					scale = Math.max(MIN_SCALE, Math.max(minScale, Math.min(MAX_SCALE, scale * zoomFactor)));

					// Re-scale the container and panel in terms of respective default width and
					// height
					rescale(scale);
					centerViewportOnPoint(e.getX(), e.getY(), oldScale, scale);
				} else {
					scrollPane.setWheelScrollingEnabled(true);
				}

				refresh();
			}
		});
		
		scrollPane.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				panel.requestFocusInWindow();
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	public void enableArrowKeyScrolling() {
		JViewport vp = scrollPane.getViewport();
		vp.setFocusable(true);
		InputMap im = vp.getInputMap(JViewport.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = vp.getActionMap();

		// UP
		im.put(KeyStroke.getKeyStroke("pressed UP"), "scrollUpPressed");
		im.put(KeyStroke.getKeyStroke("released UP"), "scrollUpReleased");
		am.put("scrollUpPressed", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				startScrolling(() -> scrollUp(1));
			}
		});
		am.put("scrollUpReleased", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				stopScrolling();
			}
		});
		// DOWN
		im.put(KeyStroke.getKeyStroke("pressed DOWN"), "scrollDownPressed");
		im.put(KeyStroke.getKeyStroke("released DOWN"), "scrollDownReleased");
		am.put("scrollDownPressed", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				startScrolling(() -> scrollUp(-1));
			}
		});
		am.put("scrollDownReleased", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				stopScrolling();
			}
		});
		// RIGHT
		im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "scrollRightPressed");
		im.put(KeyStroke.getKeyStroke("released RIGHT"), "scrollRightReleased");
		am.put("scrollRightPressed", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				startScrolling(() -> scrollRight(1));
			}
		});
		am.put("scrollRightReleased", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				stopScrolling();
			}
		});
		// LEFT
		im.put(KeyStroke.getKeyStroke("pressed LEFT"), "scrollLeftPressed");
		im.put(KeyStroke.getKeyStroke("released LEFT"), "scrollLeftReleased");
		am.put("scrollLeftPressed", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				startScrolling(() -> scrollRight(-1));
			}
		});
		am.put("scrollLeftReleased", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				stopScrolling();
			}
		});
	}

	private void startScrolling(Runnable scrollAction) {
		if (scrollTimer != null && scrollTimer.isRunning())
			return;
		scrollTimer = new Timer(20, _ -> scrollAction.run());
		scrollTimer.start();
	}

	private void stopScrolling() {
		if (scrollTimer != null) {
			scrollTimer.stop();
			scrollTimer = null;
		}
	}

	private void centerViewportOnPoint(int x, int y, double oldScale, double newScale) {
		JViewport viewport = scrollPane.getViewport();
		Point viewPos = viewport.getViewPosition();
		double offsetX = x + viewPos.x;
		double offsetY = y + viewPos.y;
		double scaleFactor = newScale / oldScale;
		int newViewX = (int) (offsetX * scaleFactor - x);
		int newViewY = (int) (offsetY * scaleFactor - y);
		viewport.setViewPosition(new Point(newViewX, newViewY));
	}

	private void rescale(double scale) {
		container.setPreferredSize(new Dimension((int) (width * scale), (int) (height * scale)));
		int newPanelW = (int) (panelWidth * scale);
		int newPanelH = (int) (panelHeight * scale);
		int newBorder = (int) (BORDER_SIZE * scale);
		panel.setBounds(newBorder, newBorder, newPanelW, newPanelH);
		container.setPreferredSize(new Dimension((int) (width * scale), (int) (height * scale)));
		bus.publish(new EVENT_ZoomChanged(scale));
	}

	private double getMinScale() {
		JViewport viewport = scrollPane.getViewport();
		Dimension viewSize = viewport.getExtentSize();
		double scaleX = (double) viewSize.width / (panelWidth + 2 * BORDER_SIZE);
		double scaleY = (double) viewSize.height / (panelHeight + 2 * BORDER_SIZE);
		return Math.max(scaleX, scaleY);
	}

	private void refresh() {
		panel.revalidate();
		panel.repaint();
	}

	public void center() {
		JViewport vp = scrollPane.getViewport();
		Dimension view = vp.getExtentSize();
		Dimension size = container.getPreferredSize();
		int x = Math.max(0, (size.width - view.width) / 2);
		int y = Math.max(0, (size.height - view.height) / 2);
		vp.setViewPosition(new Point(x, y));
	}

	public void zoom(double zoomAmount) {
		double minScale = getMinScale();
		scale = Math.min(MAX_SCALE, Math.max(Math.max(MIN_SCALE, scale + zoomAmount), minScale));
		rescale(scale);
		refresh();
	}

	public void scrollUp(int scrollAmount) {
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		int unit = vBar.getUnitIncrement();
		int newValue = vBar.getValue() - unit * scrollAmount;
		newValue = Math.max(newValue, vBar.getMinimum());

		if (newValue != vBar.getValue()) {
			vBar.setValue(newValue);
			refresh();
		}
	}

	public void scrollRight(int scrollAmount) {
		JScrollBar hBar = scrollPane.getHorizontalScrollBar();
		int unit = hBar.getUnitIncrement();
		int newValue = hBar.getValue() + unit * scrollAmount;
		newValue = Math.max(newValue, hBar.getMinimum());
		if (newValue != hBar.getValue()) {
			hBar.setValue(newValue);
			refresh();
		}
	}

	public JScrollPane getPanel() {
		return scrollPane;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = Math.min(MAX_SCALE, Math.max(MIN_SCALE, scale));
		rescale(scale);
		refresh();
	}

}