package UI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Utility.RadioButtonGroup;

public class BUTTON_RadioButton extends SuperButton {

	private int index;
	private RadioButtonGroup group;

	public BUTTON_RadioButton(int width, int height, RadioButtonGroup group, int index) {
		super(width, height);
		this.group = group;
		this.index = index;

	}

	public BUTTON_RadioButton(RadioButtonGroup group, int index) {
		super();
		this.group = group;
		this.index = index;

	}

	@Override
	public void setupButtonBehavior() {
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				group.setOnButton(index);
				run();
				updateColor();
			}
		});
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

}
