/**
 * Swaps the layer locations at the given indices
 */
package Commands;

import Panels.Canvas;

public class COMMAND_ReorderLayer implements Command {

	Canvas canvas;
	int oldIndex, newIndex;

	public COMMAND_ReorderLayer(Canvas canvas, int oldIndex, int newIndex) {
		this.canvas = canvas;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	@Override
	public void execute() {
		if (oldIndex >= 0 && oldIndex < canvas.getNumLayers() && newIndex >= 0 && newIndex < canvas.getNumLayers()) {
			canvas.swapLayers(oldIndex, newIndex);
			canvas.setCurrentLayer(newIndex);
		}

	}

	@Override
	public void undo() {
		if (oldIndex >= 0 && oldIndex < canvas.getNumLayers() && newIndex >= 0 && newIndex < canvas.getNumLayers()) {
			canvas.swapLayers(newIndex, oldIndex);
			canvas.setCurrentLayer(oldIndex);
		}
	}

	@Override
	public boolean save() {
		return true;
	}

}
