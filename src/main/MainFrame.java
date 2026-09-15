package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JFrame;

import Panels.SuperPanel;
import Panels.Canvas;
import Panels.PANEL_BrushSettings;
import Panels.PANEL_Layers;
import Panels.PANEL_Palette;
import Panels.PANEL_TextBar;
import Panels.PANEL_Tools;
import UI.MenuBar;
import Utility.EventBus;

public class MainFrame {
	
	SuperPanel westPanel = new SuperPanel(BorderLayout.WEST, true, Color.GRAY, null, false) {
		@Override
		protected void draw(Graphics2D g2) {}};
	SuperPanel eastPanel = new SuperPanel(BorderLayout.EAST, true, Color.GRAY, null, false) {
		@Override
		protected void draw(Graphics2D g2) {}};

	private JFrame frame = new JFrame() {
		private static final long serialVersionUID = 1L;

		@Override
		public void add(Component comp, Object constraints) {
			if (BorderLayout.EAST.equals(constraints)) {
				eastPanel.add(comp);
			}
			else if (BorderLayout.WEST.equals(constraints)) {
				westPanel.add(comp);
			}
			else if ("TRUE_EAST".equals(constraints)) {
				super.add(comp, BorderLayout.EAST);
			}
			else if ("TRUE_WEST".equals(constraints)) {
				super.add(comp, BorderLayout.WEST);
			}
			else {
				super.add(comp, constraints);
			}
				
		}
	};
	private EventBus bus = new EventBus();
	
	private PANEL_Palette palette;
	private PANEL_Tools tools;
	private PANEL_BrushSettings settings;
	private PANEL_TextBar textBar;
	private PANEL_Layers layers;
	private Canvas canvas;
	private MenuBar menuBar;

	
	public MainFrame() {
		palette = new PANEL_Palette(bus);
	    tools = new PANEL_Tools(bus);
	    settings = new PANEL_BrushSettings(bus);
	    textBar = new PANEL_TextBar(bus);
	    layers = new PANEL_Layers(bus);
	    canvas = new Canvas(40, 30, bus);
	    menuBar = new MenuBar(bus);
		setParameters();
		setupLayout();
		addComponents();
	}

	private void setParameters() {
		frame.setSize(1200, 800);
		frame.setTitle("Map Editor");
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(200, 200));
		frame.setFocusable(true);
	}
	
	private void setupLayout() {
		frame.setLayout(new BorderLayout());
		westPanel.setLayout(new GridLayout(2,1));
		eastPanel.setLayout(new GridLayout(2,1));
		frame.add(eastPanel.getPanel(), "TRUE_EAST");
		frame.add(westPanel.getPanel(), "TRUE_WEST");
	}
	
	
	private void addComponents() {
		frame.add(palette.getPanel(), palette.getAlignment());
		frame.add(tools.getPanel(), tools.getAlignment());
		frame.add(settings.getPanel(), settings.getAlignment());
		frame.add(layers.getPanel(), layers.getAlignment());
		frame.add(textBar.getPanel(), BorderLayout.SOUTH);
		frame.add(canvas.getPanel(), BorderLayout.CENTER);
		frame.setJMenuBar(menuBar.getMenuBar());

	}
	
	public void show() {
		frame.setVisible(true);
	}
}
