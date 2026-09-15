package Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import Events.EVENT_ColorChanged;
import UI.BUTTON_RadioButton;
import Utility.RadioButtonGroup;
import Utility.EventBus;

public class PANEL_Palette extends SuperPanel{
	
	private final Color[] COLORS = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN,
			Color.MAGENTA, Color.PINK, Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.WHITE };
	
	private final int NUM_BUTTONS = 12;
	private final int BUTTON_SIZE = 24;
	
	RadioButtonGroup buttonGroup = new RadioButtonGroup(NUM_BUTTONS, BUTTON_SIZE, BUTTON_SIZE);
	
	public PANEL_Palette(EventBus bus) {
		super(100, 0, BorderLayout.WEST, false, Color.GRAY, bus, true);
		this.bus = bus;
		setLayout(new FlowLayout());
		setUpButtons();
	}

	private void setUpButtons() {
		add(buttonGroup);
		
		for (BUTTON_RadioButton button : buttonGroup) {
			button.setColor(COLORS[button.getIndex()]);
			button.setOnClick(() -> bus.publish(new EVENT_ColorChanged(COLORS[button.getIndex()])));
		}
	}

	protected void draw(Graphics2D g2) {}
	
	public int getSelectedColor() {
		return buttonGroup.getOnButtonIndex();
	}
}
