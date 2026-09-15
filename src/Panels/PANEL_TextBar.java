package Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import Events.EVENT_MousePosition;
import Events.EVENT_ZoomChanged;
import Utility.EventBus;

public class PANEL_TextBar extends SuperPanel{
	
	public final static int LEFT = 0;
	public final static int MIDDLE = 1;
	public final static int RIGHT = 2;
	
	JLabel label1 = new JLabel();
	JLabel label2 = new JLabel();
	JLabel label3 = new JLabel();
	
	public PANEL_TextBar(EventBus bus) {
		super(BorderLayout.SOUTH, false, Color.LIGHT_GRAY, bus, true);
		getPanel().setLayout(new BorderLayout());
		addLabels();
		setEventListeners(bus);
	}
	
	private void setEventListeners(EventBus bus) {
		bus.subscribe(EVENT_MousePosition.class, e -> setText("(" + e.tileX() + ", " + e.tileY() + ")", MIDDLE));
		bus.subscribe(EVENT_ZoomChanged.class, e -> setText("" + ((int) (100 * e.zoom())) + "%", LEFT));
	}
	
	protected void draw(Graphics2D g2) {}
	
	private void addLabels() {
		getPanel().add(label1, BorderLayout.WEST);
		getPanel().add(label2, BorderLayout.CENTER);
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		getPanel().add(label3, BorderLayout.EAST);
	}
	
	
	
	public void setText(String text, int alignment) {
		switch (alignment) {
		case LEFT:
			label1.setText(text);
			break;
		case MIDDLE:
			label2.setText(text);
			break;
		case RIGHT:
			label3.setText(text);
			break;
		}
		getPanel().revalidate();
	}

}
