package Panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import Events.EVENT_LayerDeleted;
import Events.EVENT_LayerSelected;
import Events.EVENT_LayersReordered;
import Events.EVENT_NewLayerCreated;
import Events.EVENT_RequestChangeLayerIndex;
import Events.EVENT_RequestCopyLayer;
import Events.EVENT_RequestDeleteLayer;
import Events.EVENT_RequestMergeLayer;
import Events.EVENT_RequestNewLayer;
import Events.EVENT_RequestSelectLayer;
import Events.EVENT_RequestSetLayerVisible;
import UI.BUTTON_Button;
import UI.BUTTON_RadioButton;
import UI.EditableLabel;
import UI.ScrollList;
import Utility.RadioButtonGroup;
import Utility.EventBus;

public class PANEL_Layers extends SuperPanel {

	RadioButtonGroup buttonGroup = new RadioButtonGroup();

	BUTTON_Button addButton = new BUTTON_Button(25, 25);
	BUTTON_Button removeButton = new BUTTON_Button(25, 25);
	BUTTON_Button upButton = new BUTTON_Button(25, 25);
	BUTTON_Button downButton = new BUTTON_Button(25, 25);
	BUTTON_Button mergeButton = new BUTTON_Button(25, 25);
	BUTTON_Button copyButton = new BUTTON_Button(25, 25);

	ScrollList list = new ScrollList(Color.GRAY);
	JPanel buttonLayoutPanel1 = new JPanel(new GridLayout(2, 1));
	JPanel buttonLayoutPanel2 = new JPanel(new GridLayout(2, 1));
	JPanel buttonLayoutPanel3 = new JPanel(new GridLayout(2, 2));

	HashMap<BUTTON_RadioButton, Layer> buttonToLayerMap = new HashMap<>();
	HashMap<Layer, BUTTON_RadioButton> layerToButtonMap = new HashMap<>();

	public PANEL_Layers(EventBus bus) {
		super(100, 0, BorderLayout.EAST, false, Color.GRAY, bus, true);
		setLayout(new BorderLayout());
		setUpButtons();
		addListeners();
	}

	private void setUpButtons() {
		// SCROLL LIST
		list.add(buttonGroup);
		add(list.getPanel());

		// ADD NEW LAYER BUTTON
		addButton.setOnClick(() -> bus.publish(new EVENT_RequestNewLayer()));
		addButton.addText("New Layer");

		// DELETE LAYER BUTTON
		removeButton.setOnClick(() -> {
			if (!buttonGroup.isEmpty()) {
				BUTTON_RadioButton selected = buttonGroup.getOnButton();
				Layer layer = buttonToLayerMap.get(selected);

				if (selected != null) {
					bus.publish(new EVENT_RequestDeleteLayer(layer));
				}
			}
		});
		removeButton.addText("Delete Layer");

		// UP BUTTON
		upButton.setOnClick(() -> {
			if (!buttonGroup.isEmpty()) {
				BUTTON_RadioButton selected = buttonGroup.getOnButton();
				if (selected != null && selected.getIndex() > 0) {
					bus.publish(new EVENT_RequestChangeLayerIndex(selected.getIndex(), selected.getIndex() - 1));
				}
			}
		});
		upButton.addText("Shift Up");

		// DOWN BUTTON
		downButton.addText("Shift Down");
		downButton.setOnClick(() -> {
			if (!buttonGroup.isEmpty()) {
				BUTTON_RadioButton selected = buttonGroup.getOnButton();
				if (selected != null && selected.getIndex() < buttonGroup.getNumButtons() - 1) {
					bus.publish(new EVENT_RequestChangeLayerIndex(selected.getIndex(), selected.getIndex() + 1));
				}
			}
		});

		// MERGE BUTTON
		mergeButton.addText("Merge Up");
		mergeButton.setOnClick(() -> {
			if (!buttonGroup.isEmpty() && buttonGroup.getOnButtonIndex() > 0) {
				BUTTON_RadioButton selected = buttonGroup.getOnButton();
				if (selected != null) {
					bus.publish(new EVENT_RequestMergeLayer(selected.getIndex()));
				}
			}
		});
		
		// COPY BUTTON
				copyButton.addText("Copy Layer");
				copyButton.setOnClick(() -> {
					if (!buttonGroup.isEmpty() && buttonGroup.getOnButtonIndex() >= 0) {
						BUTTON_RadioButton selected = buttonGroup.getOnButton();
						if (selected != null) {
							bus.publish(new EVENT_RequestCopyLayer(selected.getIndex()));
						}
					}
				});

		// GRID LAYOUT FOR ADD/DELETE BUTTONS
		getPanel().add(buttonLayoutPanel1, BorderLayout.SOUTH);
		buttonLayoutPanel1.add(buttonLayoutPanel3);
		buttonLayoutPanel1.add(buttonLayoutPanel2);
		buttonLayoutPanel3.add(addButton.getComponent());
		buttonLayoutPanel3.add(upButton.getComponent());
		buttonLayoutPanel3.add(removeButton.getComponent());
		buttonLayoutPanel3.add(downButton.getComponent());
		buttonLayoutPanel2.add(mergeButton.getComponent());
		buttonLayoutPanel2.add(copyButton.getComponent());
	}

	private void addListeners() {
		bus.subscribe(EVENT_NewLayerCreated.class, e -> addLayerButton(e.layer(), e.index()));
		bus.subscribe(EVENT_LayerDeleted.class, e -> removeLayerButton(e.layer()));
		bus.subscribe(EVENT_LayerSelected.class, e -> selectLayerButton(e.layer()));
		bus.subscribe(EVENT_LayersReordered.class, e -> swapLayerButtons(e.index1(), e.index2()));
	}

	private void addLayerButton(Layer layer, int index) {
		// BUTTON SETUP
		BUTTON_RadioButton newBtn = buttonGroup.addButton(index);
		buttonToLayerMap.put(newBtn, layer);
		layerToButtonMap.put(layer, newBtn);

		newBtn.setOnClick(() -> bus.publish(new EVENT_RequestSelectLayer(layer)));

		// LABEL SETUP
		EditableLabel label = new EditableLabel(layer.getName()) {
			@Override
			protected void commitText() {
				super.commitText();
				layer.setName(this.getText());
			}

			@Override
			protected void onSingleClick() {
				buttonGroup.setOnButton(newBtn);
				newBtn.run();
			}
		};

		// CHECKBOX SETUP
		JCheckBox checkBox = new JCheckBox("", true);
		checkBox.setOpaque(false);
		checkBox.addActionListener(_ -> bus.publish(new EVENT_RequestSetLayerVisible(layer, checkBox.isSelected())));

		// ADD EVERYTHING TO BUTTON
		newBtn.add(label.getPanel());
		newBtn.add(checkBox);

		// ADD NEW BUTTON TO LIST
		list.add(newBtn.getComponent(), index);
	}

	private void removeLayerButton(Layer layer) {
		BUTTON_RadioButton button = layerToButtonMap.remove(layer);
		buttonToLayerMap.remove(button);
		buttonGroup.deleteButton(button);
		list.delete(button.getComponent());

	}

	private void selectLayerButton(Layer layer) {
		BUTTON_RadioButton button = layerToButtonMap.get(layer);
		buttonGroup.setOnButton(button);
	}

	private void swapLayerButtons(int index1, int index2) {
		BUTTON_RadioButton button1 = buttonGroup.getButton(index1);
		BUTTON_RadioButton button2 = buttonGroup.getButton(index2);

		button1.setIndex(index2);
		button2.setIndex(index1);

		buttonGroup.setButton(button2, index1);
		buttonGroup.setButton(button1, index2);

		buttonGroup.setOnButton(button1);
		list.swap(index1, index2);
	}

	protected void draw(Graphics2D g2) {
	}

}
