/**
 * Copying a layer creates a new layer just below the selected layer which contains exactly the same tiles as the selected layer
 */

package Commands;

import Panels.Canvas;
import Panels.Layer;

public class COMMAND_CopyLayer implements Command {

	private Canvas canvas;
	private int index;
	private Layer newLayer;

	public COMMAND_CopyLayer(Canvas canvas, int index) {
		this.canvas = canvas;
		this.index = index;
	}

	@Override
	public void execute() {
		if (index >= 0 && index < canvas.getNumLayers()) {
			newLayer = canvas.getLayer(index).clone();
			if (!newLayer.getName().endsWith("(COPY)")) newLayer.setName(newLayer.getName() + " (COPY)");
			canvas.insertLayer(newLayer, index + 1);
		}

	}

	@Override
	public void undo() {
		if (index >= 0 && index < canvas.getNumLayers() - 1) {
			canvas.deleteLayer(index + 1);
		}
	}

	@Override
	public boolean save() {
		return true;
	}

}
