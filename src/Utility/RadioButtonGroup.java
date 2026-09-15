package Utility;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import UI.BUTTON_RadioButton;

public class RadioButtonGroup implements Iterable<BUTTON_RadioButton> {

	private final List<BUTTON_RadioButton> buttons;
	private int onButton = 0;

	// Constructor with custom size buttons
	public RadioButtonGroup(int numButtons, int buttonWidth, int buttonHeight) {
		buttons = new ArrayList<>(numButtons);
		for (int i = 0; i < numButtons; i++) {
			buttons.add(new BUTTON_RadioButton(buttonWidth, buttonHeight, this, i));
		}
		if (!buttons.isEmpty())
			setOnButton(0);
	}

	// Constructor with default size buttons
	public RadioButtonGroup(int numButtons) {
		buttons = new ArrayList<>(numButtons);
		for (int i = 0; i < numButtons; i++) {
			buttons.add(new BUTTON_RadioButton(this, i));
		}
		if (!buttons.isEmpty())
			setOnButton(0);
	}

	public RadioButtonGroup() {
		this(0);
	}

	public BUTTON_RadioButton addButton() {
		BUTTON_RadioButton btn = new BUTTON_RadioButton(this, buttons.size());
		buttons.add(btn);
		if (buttons.size() == 1)
			setOnButton(0);
		updateButtons();
		return btn;
	}

	public BUTTON_RadioButton addButton(int index) {
		BUTTON_RadioButton btn = new BUTTON_RadioButton(this, index);
		buttons.add(index, btn);
		for (int i = index + 1; i < buttons.size(); i++) {
			buttons.get(i).setIndex(i);
		}
		if (buttons.size() == 1)
			setOnButton(0);
		updateButtons();
		return btn;
	}

	public void addButton(int width, int height) {
		buttons.add(new BUTTON_RadioButton(width, height, this, buttons.size()));
		if (buttons.size() == 1)
			setOnButton(0);
	}

	public void deleteButton(int index) {
		buttons.remove(index);
		updateButtons();
	}

	public void deleteButton(BUTTON_RadioButton button) {
		int idx = buttons.indexOf(button);
		if (idx >= 0) {
			buttons.remove(idx);
			for (int i = 0; i < buttons.size(); i++) {
				buttons.get(i).setIndex(i);
			}
			if (onButton >= buttons.size())
				onButton = buttons.size() - 1;
			updateButtons();
		}
	}

	public void setOnButton(int buttonIndex) {
		if (buttonIndex < 0 || buttonIndex >= buttons.size())
			return;
		this.onButton = buttonIndex;
		updateButtons();
	}

	public void setOnButton(BUTTON_RadioButton button) {
		if (!buttons.contains(button))
			return;
		onButton = buttons.indexOf(button);
		updateButtons();
	}
	
	public void setButton(BUTTON_RadioButton button, int index) {
		buttons.set(index, button);
		updateButtons();
	}

	public void updateButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			BUTTON_RadioButton btn = buttons.get(i);
			btn.setToggled(i == onButton);
			btn.updateColor();
		}
	}

	public int getOnButtonIndex() {
		return onButton;
	}

	public BUTTON_RadioButton getOnButton() {
		return buttons.get(onButton);
	}

	public int getNumButtons() {
		return buttons.size();
	}

	public BUTTON_RadioButton getButton(int index) {
		return buttons.get(index);
	}
	
	public boolean isEmpty() {
		return buttons.isEmpty();
	}

	public void draw(Graphics2D g2) {
		for (BUTTON_RadioButton button : buttons) {
			button.draw(g2);
		}
	}

	@Override
	public Iterator<BUTTON_RadioButton> iterator() {
		return new Iterator<BUTTON_RadioButton>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < buttons.size();
			}

			@Override
			public BUTTON_RadioButton next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return buttons.get(index++);
			}
		};
	}
}
