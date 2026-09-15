package UI;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class EditableLabel {
	
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel();
	private JTextField textField = new JTextField();
	private String text;
	
	private Font libertyFont;
	
	public EditableLabel(String text) {
		this.text = text;
		label.setText(text);
		textField.setVisible(false);
		panel.add(label);
		panel.add(textField);
		setFont();
		setParameters();
		enableEditing();
	}
	
	public EditableLabel(String text, int textBoxWidth) {
		this(text);
		textField.setColumns(textBoxWidth);
	}
	
	private void setParameters() {
		panel.setOpaque(false);
	}
	
	private void setFont() {
		try {
			InputStream is = getClass().getResourceAsStream("/Fonts/Liberty.ttf");
			libertyFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		label.setFont(libertyFont.deriveFont(Font.PLAIN, 28f));
		textField.setFont(libertyFont.deriveFont(Font.PLAIN, 28f));
	}
	
	private void enableEditing() {
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
	                textField.setText(label.getText());
	                textField.setColumns(textField.getText().length());
	                textField.setHorizontalAlignment(JTextField.CENTER);
	                textField.setText(textField.getText().trim());
	                label.setVisible(false);
	                textField.setVisible(true);
	                textField.requestFocusInWindow();
	                textField.selectAll();
				}
				else if (e.getClickCount() == 1) {
					onSingleClick();
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}			
		});
		textField.addActionListener(_ -> commitText());
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				commitText();
			}
		});
	}
	
	protected void commitText() {
		if (textField.getText().equals("")) textField.setText("     ");
		label.setText(textField.getText());
		text = label.getText();
		textField.setVisible(false);
		label.setVisible(true);
	}
	
	protected void onSingleClick() {}
	
	public Component getPanel() {
		return panel;
	}
	
	public String getText() {
		return text;
	}
}
