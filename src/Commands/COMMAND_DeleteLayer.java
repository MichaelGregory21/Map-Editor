/**
 * Deletes the currently selected layer
 */

package Commands;

import Panels.Canvas;
import Panels.Layer;

public class COMMAND_DeleteLayer implements Command {

	private Canvas canvas;
	private Layer layer;
	private int index;

	public COMMAND_DeleteLayer(Canvas canvas, Layer layer) {
		this.canvas = canvas;
		this.layer = layer;
	}

	@Override
	public void execute() {
		index = canvas.getIndexOfLayer(layer);
		if (index >= 0 && index < canvas.getNumLayers()) {
			canvas.deleteLayer(layer);
		}
		if (canvas.getNumLayers() == 0) {
			canvas.setCurrentLayer(-1);
		}
		else {
			canvas.setCurrentLayer(Math.min(index, canvas.getNumLayers() - 1));
		}

	}

	@Override
	public void undo() {
		if (canvas.getCurrentLayer() == -1) {
			canvas.insertLayer(layer, 0);
			canvas.setCurrentLayer(0);
		}
		else {
			canvas.insertLayer(layer, index);
			canvas.setCurrentLayer(index);
		}
	}

	@Override
	public boolean save() {
		return true;
	}

}
