package UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public abstract class SuperButton {
	
	Object data;
	
	protected JPanel button = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			if (icon != null)
				g2.drawImage(icon, 0, 0, null);
		}
	};
	protected boolean toggled = false;
	protected Color color = Color.GRAY;
	protected BufferedImage icon;
	protected int width, height;

	private Border outBorder;
	private Border inBorder;

	private Runnable onClick;
	
	Font libertyFont;
	
	public SuperButton(int width, int height) {
		this.width = width;
		this.height = height;
		outBorder = BorderFactory.createBevelBorder(0, color.brighter(), color.darker());
		inBorder = BorderFactory.createBevelBorder(0, color.darker(), color.brighter());
		setFont();
		button.setPreferredSize(new Dimension(width, height));
		updateColor();
		setupButtonBehavior();
	}
	
	public SuperButton() {
		outBorder = BorderFactory.createBevelBorder(0, color.brighter(), color.darker());
		inBorder = BorderFactory.createBevelBorder(0, color.darker(), color.brighter());
		setFont();
		setParameters();
		updateColor();
		setupButtonBehavior();
	}

	private void setParameters() {
		
		
		button.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				button.requestFocusInWindow();
			}
			@Override
		    public void mouseEntered(MouseEvent e) {
		        button.setCursor(Cursor.getDefaultCursor());
		    }

		});
	}
	
	private void setFont() {
		try {
			InputStream is = getClass().getResourceAsStream("/Fonts/Liberty.ttf");
			libertyFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public void updateColor() {
		if (!toggled) {
			button.setBackground(color);
			button.setBorder(outBorder);
		} else {
			button.setBackground(color.darker());
			button.setBorder(inBorder);
		}
		button.revalidate();
		button.repaint();
	}

	public void draw(Graphics2D g2) {
		button.repaint();
	}

	protected abstract void setupButtonBehavior();

	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}

	public void run() {
		if (onClick != null) {
			onClick.run();
		}
	}
	
	public void add(Component component) {
		button.add(component);
	}

	public void setIcon(String path) {
		try {
			icon = ImageIO.read(getClass().getResourceAsStream(path + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		button.repaint();
	}

	public void setColor(Color color) {
		this.color = color;
		updateColor();
	}
	
	public void addText(String text) {
		JLabel label = new JLabel();
		
		label.setFont(libertyFont.deriveFont(Font.PLAIN, 22f));
		label.setText(text);
		add(label);
		button.repaint();
	}

	public boolean isToggled() {
		return toggled;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		button.repaint();
	}

	public JComponent getComponent() {
		return button;
	}

}
