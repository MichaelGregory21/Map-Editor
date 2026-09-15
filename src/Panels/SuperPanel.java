package Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import Utility.RadioButtonGroup;
import UI.BUTTON_RadioButton;
import UI.EditableLabel;
import UI.SuperButton;
import Utility.EventBus;

public abstract class SuperPanel {
	private static final int RESIZE_MARGIN = 5;
	private static final int MINIMUM_SIZE = 50;
	private static final int MAXIMUM_SIZE = 300;
	
	private JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			draw(g2);
		}
	};
	protected EventBus bus;
	
	private Color panelColor;
	private String alignment;
	
	private boolean resizing = false;
	private int prevX, prevY;
	
	private boolean border;
	
	public SuperPanel(int width, int height, String alignment, boolean resizable, Color panelColor, EventBus bus, boolean border) {
		this.alignment = alignment;
		this.panelColor = panelColor;
		this.bus = bus;
		this.border = border;
		panel.setPreferredSize(new Dimension(width, height));
		setParameters(panelColor);
		if (resizable) {
			enableResizing();
		}
	}
	
	public SuperPanel(String alignment, boolean resizable, Color panelColor, EventBus bus, boolean border) {
		this.alignment = alignment;
		this.panelColor = panelColor;
		this.bus = bus;
		this.border = border;
		setParameters(panelColor);
		if (resizable) {
			enableResizing();
		}
	}
		
	private void setParameters(Color panelColor) {
		panel.setBackground(panelColor);
		if (border) {
			panel.setBorder(createBorder());
		}
		panel.setFocusable(true);
	}
	
	private Border createBorder() {
		Color highlight = panelColor.brighter();
	    Color shadow = panelColor.darker();
	    Border bevelShadow;
	    Border bevelHighlight;
	    
	    switch (alignment) {
	    case BorderLayout.NORTH:
	    	bevelShadow = BorderFactory.createMatteBorder(0, 0, 2, 0, shadow);
		    bevelHighlight = BorderFactory.createMatteBorder(0, 0, 1, 0, highlight);
	    	break;
	    case BorderLayout.EAST:
	    	bevelShadow = BorderFactory.createMatteBorder(2, 2, 2, 0, shadow);
		    bevelHighlight = BorderFactory.createMatteBorder(1, 1, 1, 0, highlight);
	    	break;
	    case BorderLayout.SOUTH:
	    	bevelShadow = BorderFactory.createMatteBorder(2, 0, 0, 0, shadow);
		    bevelHighlight = BorderFactory.createMatteBorder(1, 0, 0, 0, highlight);
	    	break;
	    default:
	    	bevelShadow = BorderFactory.createMatteBorder(2, 0, 2, 2, shadow);
		    bevelHighlight = BorderFactory.createMatteBorder(1, 0, 1, 1, highlight);
	    	break;
	    }
	    
	    Border compoundBorder = BorderFactory.createCompoundBorder(bevelShadow, bevelHighlight);
		return compoundBorder;
	}
	
	private void enableResizing() {
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (isOnResizableEdge(e)) {
					resizing = true;
					prevX = e.getXOnScreen();
					prevY = e.getYOnScreen();
				}
				panel.requestFocusInWindow();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				resizing = false;
			}
		});
		
		panel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (isOnResizableEdge(e)) {
					if (alignment == BorderLayout.WEST || alignment == BorderLayout.EAST) {
						panel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					}
					else {
						panel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					}
				}
				else {
					panel.setCursor(Cursor.getDefaultCursor());
				}
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if (resizing) {
					Dimension size = panel.getPreferredSize();
					int deltaX, deltaY;
					
					switch(alignment) {
						case BorderLayout.NORTH:
							deltaY = e.getYOnScreen() - prevY;
							size.height = Math.min(MAXIMUM_SIZE, Math.max(MINIMUM_SIZE,  size.height + deltaY));
							prevY = e.getYOnScreen();
							break;
						case BorderLayout.EAST:
							deltaX = prevX - e.getXOnScreen();
							size.width = Math.min(MAXIMUM_SIZE, Math.max(MINIMUM_SIZE,  size.width + deltaX));
							prevX = e.getXOnScreen();
							break;
						case BorderLayout.SOUTH:
							deltaY = prevY - e.getYOnScreen();
							size.height = Math.min(MAXIMUM_SIZE, Math.max(MINIMUM_SIZE,  size.height + deltaY));
							prevY = e.getYOnScreen();
							break;
						case BorderLayout.WEST:
							deltaX =  e.getXOnScreen() - prevX;
							size.width = Math.min(MAXIMUM_SIZE, Math.max(MINIMUM_SIZE,  size.width + deltaX));
							prevX = e.getXOnScreen();
							break;
					}
					resize(size);
					
				}
			}
		});
		
	}
	
	protected void resize(Dimension size) {
		panel.setPreferredSize(size);
		panel.revalidate();
	}
	
	private boolean isOnResizableEdge(MouseEvent e) {
		int width = panel.getWidth();
		int height = panel.getHeight();
		
		switch(alignment) {
		case BorderLayout.NORTH:
			return e.getY() >= height - RESIZE_MARGIN;
		case BorderLayout.EAST:
			return e.getX() <= RESIZE_MARGIN;
		case BorderLayout.SOUTH:
			return e.getY() <= RESIZE_MARGIN;
		case BorderLayout.WEST:
			return e.getX() >= width - RESIZE_MARGIN;
		}
		
		return false;
	}
	
	public void add(Component comp){
		panel.add(comp);
	}
	
	public void add(SuperButton button) {
		add(button.getComponent());
	}
	
	public void add(RadioButtonGroup group) {
		for (BUTTON_RadioButton button : group) {
			add(button);
		}
	}
	
	public void add(EditableLabel label) {
		add(label.getPanel());
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public String getAlignment() {
		return alignment;
	}
	
	public void setLayout(LayoutManager L) {
		panel.setLayout(L);
	}
	
	abstract protected void draw(Graphics2D g2);
}
