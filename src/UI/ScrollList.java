package UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Utility.RadioButtonGroup;

public class ScrollList {

	private int ITEM_SIZE = 30;
	
	private Color color;

	private ArrayList<Component> items = new ArrayList<>();
	
	private JPanel itemPanel = new JPanel();
	private JScrollPane scrollPane = new JScrollPane();

	public ScrollList(Color color) {
		this.color = color;
		setParameters();
		enableMouseListener();
	}

	

	private void setParameters() {
		itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
		itemPanel.setBackground(color);

		scrollPane.setViewportView(itemPanel);
		scrollPane.getViewport().setBackground(color);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollPane.getVerticalScrollBar().setUnitIncrement(25);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(25);
		
	}
	
	private void enableMouseListener() {
		itemPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				itemPanel.requestFocusInWindow();
				
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
		itemPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				itemPanel.setCursor(Cursor.getDefaultCursor());
				
			}
			
		});
		
	}

	public void add(JComponent comp) {
		items.add(comp);
		comp.setMinimumSize(new Dimension(0, ITEM_SIZE));
		comp.setPreferredSize(new Dimension(scrollPane.getViewport().getWidth(), ITEM_SIZE));
		comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_SIZE)); 
		comp.setAlignmentX(Component.LEFT_ALIGNMENT);
		itemPanel.add(comp);
		refresh();
	}
	
	public void add(JComponent comp, int index) {
		items.add(index, comp);
		comp.setMinimumSize(new Dimension(0, ITEM_SIZE));
		comp.setPreferredSize(new Dimension(scrollPane.getViewport().getWidth(), ITEM_SIZE));
		comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_SIZE)); 
		comp.setAlignmentX(Component.LEFT_ALIGNMENT);
		itemPanel.add(comp, index);
		refresh();
	}
	
	public void delete(Component comp) {
		items.remove(comp);
		itemPanel.remove(comp);
		refresh();
	}

	public void add(RadioButtonGroup buttonGroup) {
		for (BUTTON_RadioButton button : buttonGroup) {
			add(button.getComponent());
		}
	}
	
	public void swap(int index1, int index2) {
		Component item1 = items.get(index1);
		Component item2 = items.get(index2);
		items.set(index1, item2);
		items.set(index2, item1);
		refresh();
	}

	public JScrollPane getPanel() {
		return scrollPane;
	}

	public void setDimension(int width, int height) {
		scrollPane.setPreferredSize(new Dimension(width, height));
	}
	
	public void refresh() {
		for (Component comp : items) {
			itemPanel.remove(comp);
		}
		for (Component comp : items) {
			itemPanel.add(comp);
		}
		itemPanel.revalidate();
		itemPanel.repaint();
	}
}
