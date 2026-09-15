package Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import Events.EVENT_BrushSizeSet;
import Events.EVENT_RequestBrushSizeChange;
import Events.EVENT_RequestBrushSizeSet;
import Events.EVENT_ShapeChanged;
import Tools.TOOL_Brush;
import UI.BUTTON_Button;
import Utility.EventBus;
import Utility.RadioButtonGroup;

public class PANEL_BrushSettings extends SuperPanel {
	
	private final int BUTTON_SIZE = 24;
	private final int NUM_SHAPE_BUTTONS = 3;
	
	BUTTON_Button increase = new BUTTON_Button(BUTTON_SIZE, BUTTON_SIZE);
	BUTTON_Button decrease = new BUTTON_Button(BUTTON_SIZE, BUTTON_SIZE);
	
	TextField brushSize = new TextField(Integer.toString(0));
	
	RadioButtonGroup shapeGroup = new RadioButtonGroup(NUM_SHAPE_BUTTONS, BUTTON_SIZE, BUTTON_SIZE);

	public PANEL_BrushSettings(EventBus bus) {
		super(BorderLayout.NORTH, false, Color.GRAY, bus, true);
		setLayout(new FlowLayout());
		setUpButtons();
	}
	
	private void setUpButtons() {
		bus.subscribe(EVENT_BrushSizeSet.class, e -> brushSize.setText(Integer.toString(e.size())));
		
		brushSize.setBackground(Color.LIGHT_GRAY);
		brushSize.setFocusable(true);
		brushSize.addKeyListener(new KeyAdapter() {
			@Override
		    public void keyTyped(KeyEvent e) {
		        char c = e.getKeyChar();
		        // Only allow digits, backspace, & enter
		        if (!Character.isDigit(c) && c != '\b' && !Character.isISOControl(c) ) {
		            e.consume();
		        }
		    }
		});
		brushSize.addActionListener(_ -> {
			bus.publish(new EVENT_RequestBrushSizeSet(Math.max(0, Math.min(10, Integer.parseInt(brushSize.getText())))));
			});
		
		shapeGroup.getButton(0).setIcon("/ButtonIcons/square");	
		shapeGroup.getButton(0).setOnClick(() -> bus.publish(new EVENT_ShapeChanged(TOOL_Brush.SQUARE)));
		
		shapeGroup.getButton(1).setIcon("/ButtonIcons/round");
		shapeGroup.getButton(1).setOnClick(() -> bus.publish(new EVENT_ShapeChanged(TOOL_Brush.ROUND)));
		
		shapeGroup.getButton(2).setIcon("/ButtonIcons/diamond");
		shapeGroup.getButton(2).setOnClick(() -> bus.publish(new EVENT_ShapeChanged(TOOL_Brush.DIAMOND)));
		
		increase.setIcon("/ButtonIcons/increase");
		increase.setOnClick(() -> bus.publish(new EVENT_RequestBrushSizeChange(1)));
		
		decrease.setIcon("/ButtonIcons/decrease");
		decrease.setOnClick(() -> bus.publish(new EVENT_RequestBrushSizeChange(-1)));
		
		add(brushSize);
		
		add(increase);
		add(decrease);
		
		add(shapeGroup);
		
	}

	@Override
	protected void draw(Graphics2D g2) {}

}
