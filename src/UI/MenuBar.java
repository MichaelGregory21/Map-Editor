package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import Events.*;
import Utility.EventBus;

public class MenuBar {
	
	private final int DELAY = 50; //ms
	
	private JMenuBar menuBar = new JMenuBar();
	private EventBus bus;
	
	
	
	public MenuBar(EventBus bus) {
		this.bus = bus;
		createMenus();
	}
	
	private void createMenus() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem newFile = new JMenuItem("New");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveAs = new JMenuItem("Save As");
		JMenuItem load = new JMenuItem("Load");
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}
		});
		
		fileMenu.add(newFile);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.add(load);
		fileMenu.add(exit);
		
		JMenuItem selectAll = new JMenuItem("Select All");
		selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_SelectAll());	
				tempDisableItem(selectAll);
			}
		});		
		JMenuItem deselect = new JMenuItem("Deselect");
		deselect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		deselect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_DeselectMask());	
				tempDisableItem(deselect);
			}
		});		
		JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		cut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_CutMask());	
				tempDisableItem(cut);
			}
		});	
		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_CopyMask());	
				tempDisableItem(copy);
			}
		});	
		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		paste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_PasteMask());	
				tempDisableItem(paste);
			}
		});	
		JMenu editMenu = new JMenu("Edit");
		JMenuItem undo = new JMenuItem("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_UndoCanvas());		
				tempDisableItem(undo);
			}
		});
		JMenuItem redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		redo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_RedoCanvas());	
				tempDisableItem(redo);
			}
		});
		
		
		editMenu.add(selectAll);
		editMenu.add(deselect);
		editMenu.add(copy);
		editMenu.add(cut);
		editMenu.add(paste);
		editMenu.add(undo);
		editMenu.add(redo);
		
		
		
		JMenu viewMenu = new JMenu("View");
		
		JMenuItem zoomIn = new JMenuItem("Zoom In");
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_ChangeZoom(+0.1));
				tempDisableItem(zoomIn);
			}
		});
		JMenuItem zoomOut = new JMenuItem("Zoom Out");
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_ChangeZoom(-0.1));
				tempDisableItem(zoomOut);
			}
		});
		JMenuItem actualSize = new JMenuItem("Actual Size");
		actualSize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		actualSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_SetZoom(1.0));
				tempDisableItem(actualSize);
			}
		});
		JMenuItem center = new JMenuItem("Center");
		center.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		center.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bus.publish(new EVENT_CenterCanvas());
				tempDisableItem(center);
			}
		});

		viewMenu.add(zoomIn);
		viewMenu.add(zoomOut);
		viewMenu.add(actualSize);
		viewMenu.add(center);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);		
	}
	
	public JMenuBar getMenuBar() {
		return menuBar;
	}
	
	private void tempDisableItem(JMenuItem item) {
		item.setEnabled(false);
		Timer timer = new Timer(DELAY, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				item.setEnabled(true);
			}
			
		});
		timer.setRepeats(false);
		timer.start();
	}
}
