package Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import Events.EVENT_ToolChanged;
import Utility.RadioButtonGroup;
import Utility.EventBus;

public class PANEL_Tools extends SuperPanel {

	private final int NUM_TOOL_BUTTONS = 6;
	private final int BUTTON_SIZE = 24;
	
	private int brushSize = 1;

	RadioButtonGroup toolGroup = new RadioButtonGroup(NUM_TOOL_BUTTONS, BUTTON_SIZE, BUTTON_SIZE);
	
	

	public PANEL_Tools(EventBus bus) {
		super(160, 50, BorderLayout.EAST, false, Color.GRAY, bus, true);
		setLayout(new FlowLayout());
		setUpButtons(); 
	}

	private void setUpButtons() {
		add(toolGroup);
		
		toolGroup.getButton(0).setIcon("/ButtonIcons/brush");	
		toolGroup.getButton(0).setOnClick(() -> bus.publish(new EVENT_ToolChanged(0)));
		
		toolGroup.getButton(1).setIcon("/ButtonIcons/bucket");
		toolGroup.getButton(1).setOnClick(() -> bus.publish(new EVENT_ToolChanged(1)));
		
		toolGroup.getButton(2).setIcon("/ButtonIcons/eraser");
		toolGroup.getButton(2).setOnClick(() -> bus.publish(new EVENT_ToolChanged(2)));
		
		toolGroup.getButton(3).setIcon("/ButtonIcons/hand");
		toolGroup.getButton(3).setOnClick(() -> bus.publish(new EVENT_ToolChanged(3)));
		
		toolGroup.getButton(4).setIcon("/ButtonIcons/selector");
		toolGroup.getButton(4).setOnClick(() -> bus.publish(new EVENT_ToolChanged(4)));
		
		toolGroup.getButton(5).setIcon("/ButtonIcons/wand");
		toolGroup.getButton(5).setOnClick(() -> bus.publish(new EVENT_ToolChanged(5)));
		
		
	}

	protected void draw(Graphics2D g2) {
		toolGroup.draw(g2);

	}

	public int getSelectedTool() {
		return toolGroup.getOnButtonIndex();
	}
	
	public int getBrushSize() {
		return brushSize;
	}

}
